# Hướng dẫn tạo Firebase cho Fast Trạm Sạc Nhanh

Tài liệu này hướng dẫn từ đầu đến cuối: tạo dự án Firebase, nối app Android, bật đăng nhập Google và email/mật khẩu, tạo Firestore, và cách để dữ liệu của cùng một tài khoản đồng bộ giữa nhiều máy.

- Tên ứng dụng: **Fast Trạm Sạc Nhanh**
- Tên gói: `com.thinhnk.fastapp`
- Gói Firebase: **Spark (miễn phí)**
- Dịch vụ dùng ở giai đoạn 1: Authentication, Cloud Firestore, Hosting (cho dữ liệu trạm), Remote Config, Crashlytics
- Không dùng Cloud Storage, Cloud Functions, Phone Auth (đều cần gói Blaze)

---

## 0. Chuẩn bị

Cần có:

1. Một tài khoản Google (dùng chính tài khoản sẽ quản lý dự án).
2. Android Studio đã cài, mở được project này.
3. Điện thoại Android thật hoặc emulator có Google Play Services (để đăng nhập Google).
4. Kết nối mạng.

Không cần thẻ tín dụng cho gói Spark.

---

## 1. Dự án Firebase đã có sẵn

Dự án đã được tạo, không cần tạo lại:

- **Project ID:** `vku-laptrinhdidong-email-auth`
- **Trang tổng quan:** <https://console.firebase.google.com/project/vku-laptrinhdidong-email-auth/overview>
- **Authentication → Sign-in method:** <https://console.firebase.google.com/project/vku-laptrinhdidong-email-auth/authentication/providers>
- **Project settings (thêm app, SHA-1):** <https://console.firebase.google.com/project/vku-laptrinhdidong-email-auth/settings/general>
- **Firestore:** <https://console.firebase.google.com/project/vku-laptrinhdidong-email-auth/firestore>

Từ đây làm tiếp **mục 2** (thêm ứng dụng Android) rồi **mục 3** (bật provider).

> Nếu sau này cần tạo dự án khác: **Create a project** → đặt tên → có thể tắt Google Analytics → **Create project**. Project ID không đổi được sau khi tạo.

---

## 2. Thêm ứng dụng Android vào dự án

1. Mở <https://console.firebase.google.com/project/vku-laptrinhdidong-email-auth/settings/general>.
2. Kéo xuống **Your apps** → bấm biểu tượng **Android** (hoặc **Add app**).
3. Điền:
   - **Android package name**: `com.thinhnk.fastapp`
     (phải khớp chính xác `applicationId` trong `app/build.gradle.kts`)
   - **App nickname**: `Fast Tram Sac Nhanh`
   - **Debug signing certificate SHA-1**: dán mã SHA-1 (cách lấy ở mục 2.1)
4. Bấm **Register app**.
5. Tải file `google-services.json`.
6. Chép file vào thư mục:

   ```
   FAST-RECHARGE/app/google-services.json
   ```

   File này không chứa mật khẩu nhưng không nên đưa lên GitHub công khai. Nếu đưa lên, thêm vào `.gitignore` và mỗi người tự tải từ Firebase console.

6. Bấm **Next** qua các bước SDK. Project này đã cấu hình sẵn Gradle, nên chỉ cần file JSON.

### 2.1. Lấy SHA-1 khi chưa có project Android

Cách 1 — dùng keytool:

```bash
keytool -list -v \
  -keystore ~/.android/debug.keystore \
  -alias androiddebugkey \
  -storepass android \
  -keypass android
```

Cách 2 — sau khi mở project bằng Android Studio:

```bash
./gradlew signingReport
```

Tìm dòng `SHA1` trong mục `debug`.

SHA-1 debug của máy đang dựng project này (dùng để đăng ký app trên Firebase):

```
E6:0B:6A:FD:6E:EB:62:74:EE:18:AB:B4:65:B3:50:F1:CE:42:93:AC
```

Máy khác hoặc keystore khác sẽ có SHA-1 khác; chạy lại `./gradlew signingReport` để lấy.

**Quan trọng:** khi phát hành bản thật, phải thêm SHA-1 của release keystore vào Firebase. Nếu không, đăng nhập Google trên bản phát hành sẽ lỗi `DEVELOPER_ERROR` hoặc `10`.

Có thể thêm nhiều SHA-1 trong **Project settings → Your apps → Add fingerprint**.

---

## 3. Bật Authentication

1. Menu trái: **Build → Authentication**.
2. Bấm **Get started**.
3. Tab **Sign-in method**, bật ba phương thức:

### 3.1. Email/Password

- Bấm **Email/Password** → **Enable**.
- **Email link (passwordless sign-in)**: để tắt.
- Bấm **Save**.

Trong **Authentication → Settings → User actions**, có thể bật:

- **Email enumeration protection**: nên bật để không lộ email nào đã đăng ký.
- **One account per email address**: giữ bật (mặc định). Điều này ngăn hai tài khoản khác provider dùng chung một email, nhưng **không tự liên kết** Google với Email/Password. Xem mục 6.

### 3.2. Google

- Bấm **Google** → **Enable**.
- **Project public-facing name**: `Fast Trạm Sạc Nhanh`
- **Project support email**: email của bạn.
- Bấm **Save**.

Sau khi bật, mở lại provider Google để lấy **Web SDK configuration → Web client ID**. Chuỗi có dạng:

```
123456789012-abcdefghijklmnop.apps.googleusercontent.com
```

Chuỗi này cần cho Credential Manager trên Android. Chép vào:

```
app/src/main/res/values/strings.xml
```

```xml
<string name="google_web_client_id">DÁN_WEB_CLIENT_ID_VÀO_ĐÂY</string>
```

Nếu chưa dán, nút Google sẽ báo chưa cấu hình thay vì crash.

### 3.3. Anonymous (khách)

- Bấm **Anonymous** → **Enable** → **Save**.

Khách xem bản đồ không cần đăng nhập. Khi khách bấm Lưu, app mời đăng nhập; nếu vẫn chọn khách, có thể dùng anonymous uid.

---

## 4. Tạo Cloud Firestore

1. Menu trái: **Build → Firestore Database**.
2. Bấm **Create database**.
3. Chọn **Production mode** (không chọn test mode).
4. Chọn vị trí: **asia-southeast1 (Singapore)**. Vị trí không đổi được sau khi tạo.
5. Bấm **Enable**.

### 4.1. Security Rules ban đầu

Mở tab **Rules**, dán:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Mỗi người chỉ đọc ghi dữ liệu của chính mình
    match /users/{uid} {
      allow read, write: if request.auth != null
                         && request.auth.uid == uid;

      match /{document=**} {
        allow read, write: if request.auth != null
                           && request.auth.uid == uid;
      }
    }

    // Giai đoạn 1 chưa có bảng cộng đồng; chặn tất cả đường còn lại
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

Bấm **Publish**.

Khi làm giai đoạn 2 (đóng góp, đánh giá) sẽ mở thêm rule riêng, không nới lỏng các rule trên.

### 4.2. Cấu trúc dữ liệu người dùng

```
users/{uid}
  displayName, email, createdAt, updatedAt
  settings { lang, units, theme, navApp, favAlerts }
  vehicles/{vehicleId}
```

`{uid}` là UID do Firebase Auth sinh ra. **Đây là chìa khóa của việc đồng bộ:** cùng một tài khoản đăng nhập ở máy nào cũng có cùng `uid`, nên cùng đọc ghi một tài liệu.

Giai đoạn 1 **chưa** đưa `savedLists` lên Firestore. Danh sách đã lưu nằm trong Room trên từng máy; màn 4 hiển thị "Đồng bộ tài khoản · Đang phát triển".

---

## 5. Kiểm tra app đã nối Firebase

Sau khi có `google-services.json` và Web client ID:

1. Build bằng command line (không cần Android Studio):

   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
   ./gradlew assembleDebug
   ```

2. Cắm điện thoại thật qua USB, bật **USB debugging**, kiểm tra:

   ```bash
   adb devices
   ```

   Phải thấy một dòng `device`, không phải `unauthorized` hay rỗng.

3. Cài và chạy:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. Mở màn Đăng nhập:
   - Đăng ký bằng email/mật khẩu → kiểm tra trong **Authentication → Users** thấy user mới.
   - Đăng nhập Google → chọn tài khoản → kiểm tra user mới.
   - Đăng nhập ẩn danh → kiểm tra user có `Anonymous`.
5. Trong Firestore, kiểm tra tài liệu `users/{uid}` được tạo sau lần đăng nhập đầu.
6. Đăng nhập cùng tài khoản trên máy thứ hai → cùng `uid` → thấy cùng dữ liệu.

Nếu app báo "Firebase chưa được cấu hình", nghĩa là chưa có `google-services.json` hoặc file đặt sai thư mục.

---

## 6. Đồng bộ Google và Email: điều quan trọng nhất

Firebase coi **Google** và **Email/Password** là hai nhà cung cấp khác nhau. Cùng một địa chỉ `a@gmail.com` có thể tạo ra hai user khác nhau nếu không xử lý đúng:

| Cách đăng nhập | Kết quả |
|---|---|
| Google `a@gmail.com` | uid `AAA` |
| Email/mật khẩu `a@gmail.com` | uid `BBB` (nếu chưa liên kết) |

Hai uid khác nhau → hai tài liệu `users/{uid}` khác nhau → **không đồng bộ**.

### 6.1. Cách đúng: liên kết hai phương thức vào một user

Kịch bản: người dùng đã có tài khoản email/mật khẩu, sau đó muốn thêm Google.

1. Đăng nhập bằng email/mật khẩu.
2. Lấy Google ID token qua Credential Manager.
3. Gọi:

```kotlin
val credential = GoogleAuthProvider.getCredential(idToken, null)
FirebaseAuth.getInstance().currentUser!!
    .linkWithCredential(credential)
    .await()
```

Sau khi liên kết, user cũ đăng nhập được bằng cả hai cách và vẫn giữ nguyên `uid`.

### 6.2. Kịch bản ngược: người dùng bấm Google trước

Nếu Firebase trả về `FirebaseAuthUserCollisionException` với mã `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL`:

1. Hiện thông báo: "Email này đã có tài khoản. Hãy đăng nhập bằng mật khẩu rồi liên kết Google trong Hồ sơ."
2. Người dùng đăng nhập email/mật khẩu.
3. Vào **Hồ sơ → Liên kết Google** và chạy `linkWithCredential` như trên.

Không tự động gộp hai tài khoản. Không xóa tài khoản này để tạo tài khoản kia, vì sẽ mất dữ liệu.

### 6.3. Quy ước của app

- Hiển thị rõ phương thức đã liên kết trong Hồ sơ: `Google`, `Email`, `Ẩn danh`.
- Nếu user đăng nhập Google mà email đã tồn tại dưới dạng email/mật khẩu, hiện hướng dẫn liên kết thay vì tạo user mới.
- Không cho đổi email đăng nhập trực tiếp; dùng luồng xác minh của Firebase nếu cần.

---

## 7. Xác minh email và quên mật khẩu

- Gửi xác minh: `user.sendEmailVerification()`.
- Quên mật khẩu: `auth.sendPasswordResetEmail(email)`.
- Cấu hình email gửi đi trong **Authentication → Templates**.
- Ở gói Spark, Firebase có hạn mức gửi email miễn phí; đủ cho đồ án.

---

## 8. Những lỗi thường gặp

| Mã / hiện tượng | Nguyên nhân | Cách sửa |
|---|---|---|
| `DEVELOPER_ERROR` / `10` | SHA-1 chưa thêm vào Firebase | Thêm SHA-1 debug/release, tải lại `google-services.json` |
| `CONFIGURATION_NOT_FOUND` | Chưa bật provider | Bật Google hoặc Email/Password trong Authentication |
| `ERROR_INVALID_CREDENTIAL` | Sai mật khẩu hoặc user chưa tồn tại | Kiểm tra lại; không tiết lộ user có tồn tại hay không |
| `ERROR_EMAIL_ALREADY_IN_USE` | Email đã đăng ký | Chuyển sang đăng nhập hoặc quên mật khẩu |
| `ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL` | Cùng email nhưng khác provider | Liên kết tài khoản theo mục 6 |
| `PERMISSION_DENIED` khi ghi Firestore | Rule chặn hoặc chưa đăng nhập | Kiểm tra `request.auth.uid == uid` |
| App crash khi mở | Thiếu `google-services.json` | Đặt file đúng `app/google-services.json`, sync lại |
| Google không hiện tài khoản | Emulator thiếu Google Play Services | Dùng emulator có Play Store hoặc máy thật |

---

## 9. Hạn mức gói Spark (miễn phí)

| Dịch vụ | Hạn mức |
|---|---|
| Authentication | Không giới hạn đăng nhập Google/email/ẩn danh |
| Firestore | 1 GB lưu trữ, 50.000 lượt đọc/ngày, 20.000 lượt ghi/ngày |
| Hosting | 10 GB lưu trữ, 360 MB/ngày băng thông |
| Remote Config | Miễn phí |
| Crashlytics | Miễn phí |

Lưu ý: **không bật Cloud Storage và Cloud Functions** trong giai đoạn 1 vì cần gói Blaze.

---

## 10. Việc cần làm sau khi tạo xong

- [ ] Đã có `app/google-services.json`
- [ ] Đã dán `google_web_client_id` vào `strings.xml`
- [ ] Đã bật Google, Email/Password, Anonymous
- [ ] Đã tạo Firestore ở `asia-southeast1`
- [ ] Đã publish Security Rules
- [ ] Đăng ký và đăng nhập thử trên máy thật/emulator
- [ ] Đăng nhập cùng tài khoản trên máy thứ hai và kiểm tra cùng `uid`
- [ ] Thêm SHA-1 release trước khi phát hành

---

## 11. Tài liệu chính thức

- Firebase Authentication: <https://firebase.google.com/docs/auth/android/start>
- Đăng nhập Google với Credential Manager: <https://developer.android.com/identity/sign-in/credential-manager>
- Liên kết tài khoản: <https://firebase.google.com/docs/auth/android/account-linking>
- Firestore rules: <https://firebase.google.com/docs/firestore/security/get-started>
- Hạn mức Spark: <https://firebase.google.com/docs/firestore/quotas>
