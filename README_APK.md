# Nudge Native Android App

## كيفية إنشاء ملف الـ APK وتثبيته على هاتفك (How to Build APK)

### الطريقة 1: التوليد التلقائي المجاني عبر GitHub Actions (بدون أندرويد ستوديو)
1. ارفع هذا المجلد إلى مستودع جديد على حسابك في GitHub (New Repository).
2. افتح تبويب **Actions** في GitHub.
3. ستجد ملف العمل المرفق `Build Nudge Android APK` يبدأ تلقائياً.
4. بمجرد انتهاء البناء (يستغرق دقيقتين)، اضغط على **Artifacts** وحمل ملف `NudgeApp-debug.apk` مباشرة وثبته على هاتفك!

### الطريقة 2: عبر أندرويد ستوديو (Android Studio)
1. افتح Android Studio واختر **Open Existing Project** وحدد هذا المجلد.
2. انتظر ثوانٍ لمزامنة Gradle.
3. من القائمة العلوية اضغط: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. سيظهر لك إشعار: `locate APK`، انسخ الملف لهاتفك واستمتع بالنغزات!