# Loan Calculation Plus

Loan Calculation Plus là ứng dụng Android hỗ trợ tính toán khoản vay, tiền gửi và các tiện ích tài chính trong một giao diện thống nhất. Ứng dụng được xây dựng bằng Kotlin, Jetpack Compose, Hilt và Room, dựa trên bố cục và luồng tương tác trong `loan-calculation-plus-mockup.html` cùng thư mục `screenshot/`.

## Tính năng chính

- Tính Personal Loan, Business Loan và Auto Loan.
- Tính Mortgage với các trường Home Price, Down Payment theo số tiền và phần trăm, Loan Term, Interest Rate, Property Tax, PMI, HOA Fees và Home insurance.
- Tính Fixed Deposit (FD) và Recurring Deposit (RD).
- Chọn tiền tệ trong form Loan và Exchange Rate, có cờ quốc gia để nhận diện nhanh.
- Tra cứu tỷ giá trực tuyến với khả năng đổi cả Base và Target currency.
- Cache tỷ giá trong Room và dùng dữ liệu đã lưu khi thiết bị tạm thời offline.
- Compare theo 4 nhóm loan, History, xem Result và chia sẻ kết quả dưới dạng PDF.
- World Clock với danh sách thành phố và múi giờ lưu cục bộ.
- Hỗ trợ quảng cáo, consent, startup flow và IAP thông qua `base-application`.

## Luồng sử dụng

```text
Startup AAR -> Consent -> Interstitial/Language/IAP -> Intro -> Home
Home -> Calculator -> Result -> Compare / Share PDF
Home -> History -> Result
Tools -> Exchange Rate / Unit Converter / World Clock
Bottom navigation -> Home / Tools / Compare / Setting
```

## Kiến trúc

- `ui/`: màn hình Compose, navigation và theme.
- `data/finance/`: model, calculator, repository và exchange-rate API.
- `data/db/`: Room database, DAO và các entity cho History, Compare, Clock và rate cache.
- `advertisement/`: native ad, interstitial và ad placement.
- `base-application/`: thư viện AAR chứa startup, IAP, ads, consent và notification flow.

## Exchange Rate API

Ứng dụng mặc định dùng GBP làm Base và USD làm Target. API hiện tại:

```text
https://open.er-api.com/v6/latest/{BASE}
```

Các mã tiền tệ được dùng chung giữa Loan và Exchange Rate, gồm GBP, USD, EUR, VND, JPY, AUD, THB, IDR, INR, CNY và các mã phổ biến khác.

## Chạy dự án

Yêu cầu JDK 17 và Android SDK 35.

Khôi phục các file môi trường cục bộ trước khi build:

- `local.properties` chứa đường dẫn Android SDK.
- `app/google-services.json` từ Firebase project tương ứng.
- Keystore release nếu cần build release.

Build và test debug:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

APK debug được tạo tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Tài liệu tích hợp

- `08_HUONG_DAN_TICH_HOP_base-application.md`: hướng dẫn tích hợp AAR.
- `base-application/HUONG_DAN_TICH_HOP.md`: tài liệu đi kèm thư viện.
- `02_PLAYBOOK_TICH_HOP.md`: quy trình tích hợp tổng quát.
- `03_ADS.md`, `05_ADS_RUNTIME_LESSONS.md`, `06_ADS_REUSABLE_COMPONENTS.md`: vận hành và tái sử dụng quảng cáo.
- `07_CHECKLIST.md`: checklist trước khi phát hành.
- `IMPLEMENTATION_PLAN.md`: kế hoạch và phạm vi triển khai ban đầu.

## Git và file cục bộ

Các file build, cache IDE, cấu hình local, signing key và `google-services.json` được loại khỏi Git bằng `.gitignore`. Source code, resource, AAR, mockup, screenshot và tài liệu tích hợp được giữ lại để phục vụ bàn giao và tái triển khai.
