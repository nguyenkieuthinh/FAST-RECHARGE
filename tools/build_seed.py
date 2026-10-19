#!/usr/bin/env python3
"""Chuyển FASTS.geojson của EVCS thành seed JSON nén cho app.

Chỉ ánh xạ trường, không sửa giá trị gốc:
- giữ nguyên tên, địa chỉ, tọa độ, trạng thái
- tách nhà vận hành, công suất và số cổng từ mô tả
- sinh ID ổn định từ tên + tọa độ

Dùng: python3 tools/build_seed.py docs/FASTS.geojson app/src/main/assets/stations_seed.json

Lưu ý: không đặt tên đích có đuôi .gz. AAPT2 sẽ tự giải nén asset .gz
và bỏ đuôi khi đóng gói, khiến code không mở được đúng tên file.
"""
import gzip
import hashlib
import json
import re
import sys
from pathlib import Path


def stable_id(name: str, lat: float, lng: float) -> str:
    raw = f"{name}|{lat:.6f}|{lng:.6f}".encode("utf-8")
    return "st_" + hashlib.sha1(raw).hexdigest()[:10]


def parse_powers(label: str, charger_text: str) -> list[float]:
    text = label or charger_text
    found = re.findall(r"(\d+(?:\.\d+)?)\s*kW", text, flags=re.IGNORECASE)
    values = sorted({float(v) for v in found})
    return values


def parse_ports(charger_text: str) -> int:
    counts = re.findall(r"có\s+(\d+)\s+cổng", charger_text, flags=re.IGNORECASE)
    return sum(int(c) for c in counts)


def parse_power_ports(charger_text: str) -> list[list[float]]:
    """Số cổng theo từng mức công suất.

    Nguồn ghi kiểu "✦ 60kW có 2 cổng sạc" nên tách được từng cặp
    (công suất, số cổng) thay vì chỉ có tổng số cổng.
    """
    pairs = re.findall(
        r"(\d+(?:\.\d+)?)\s*kW[^\n]*?có\s+(\d+)\s+cổng",
        charger_text,
        flags=re.IGNORECASE,
    )
    return [[float(power), int(count)] for power, count in pairs]


def parse_updated(note: str) -> str:
    """Ngày cập nhật trong nguồn: "Cập nhật ngày 29/6/2026"."""
    match = re.search(r"Cập nhật ngày\s*(\d{1,2}/\d{1,2}/\d{4})", note, flags=re.IGNORECASE)
    return match.group(1) if match else ""


def map_status(value: str) -> str:
    if "bảo trì" in value.lower():
        return "maintenance"
    if "bận" in value.lower():
        return "busy"
    return "active"


def main() -> None:
    if len(sys.argv) != 3:
        raise SystemExit(__doc__)

    source = Path(sys.argv[1])
    target = Path(sys.argv[2])
    data = json.loads(source.read_text(encoding="utf-8"))

    stations = []
    for feature in data["features"]:
        geometry = feature.get("geometry") or {}
        coords = geometry.get("coordinates") or []
        if len(coords) < 2:
            continue
        lng, lat = float(coords[0]), float(coords[1])
        props = feature.get("properties") or {}
        name = (props.get("name") or "").strip() or "Trạm sạc chưa có tên"
        address = (props.get("Địa chỉ:") or "").strip()
        operator = (props.get("Trụ sạc:") or "").split("\n")[0].strip()
        operator = re.split(r"\s+-\s+", operator)[0].strip() or "Khác"
        label = (props.get("Nhãn:") or "").strip()
        charger_text = props.get("Trụ sạc:") or ""
        powers = parse_powers(label, charger_text)
        open_hours = (props.get("Hoạt động:") or "").strip()
        stations.append({
            "id": stable_id(name, lat, lng),
            "name": name,
            "address": address,
            "lat": lat,
            "lng": lng,
            "operator": operator,
            "powers": powers,
            "powerPorts": parse_power_ports(charger_text),
            "ports": parse_ports(charger_text),
            "status": map_status((props.get("Trạng thái:") or "").strip()),
            "openHours": open_hours,
            "updatedAt": parse_updated((props.get("Ghi chú:") or "").strip()),
            "source": "evcs.vn",
        })

    target.parent.mkdir(parents=True, exist_ok=True)
    payload = json.dumps(stations, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
    if target.suffix == ".gz":
        with gzip.open(target, "wb", compresslevel=9) as fh:
            fh.write(payload)
    else:
        target.write_bytes(payload)
    print(f"{len(stations)} trạm -> {target} ({target.stat().st_size:,} bytes)")


if __name__ == "__main__":
    main()
