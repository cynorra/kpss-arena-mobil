# Play Store Mağaza Girişi (Store Listing)

Hazırlanma tarihi: 2026-09-14, son güncelleme: 2026-09-16. Uygulama sürümü: 1.0.1 (`app/build.gradle`).
Kaynak: `app/src/main/res/values/strings.xml`, `values-en/strings.xml`, `arrays.xml` — uydurma özellik yok, hepsi koddan doğrulandı.

## Başlık (30 karakter sınırı)

```
KPSS Arena - Deneme Soru Test
```

(29/30 karakter. Önceki başlık sadece "KPSS Arena" idi — 20 karakterlik keyword alanı boşa gidiyordu. Bağlaçsız üç anahtar kelime (deneme, soru, test) sığdırıldı — bundan fazlası hem sığmıyor hem Play'in keyword-stuffing politikasına yaklaşıyor, ek SEO kazancı açıklamalardan gelmeli.)

## Kısa Açıklama — TR (77/80 karakter)

```
Soru bankası, günlük test, 1v1 Arena ve deneme sınavlarıyla KPSS'ye hazırlan!
```

## Short Description — EN (74/80 characters)

```
Prep for KPSS with a question bank, daily tests, 1v1 battles & mock exams!
```

## Tam Açıklama — TR (1449/4000 karakter)

```
KPSS Arena: soru bankası, günlük test ve deneme sınavı bir arada — sınava hazırlanmak artık yalnız değil, rekabetçi ve eğlenceli!

Türkiye'nin dört bir yanındaki adaylarla aynı anda yarış, gelişimini takip et, arkadaşlarına meydan oku. Reklamla desteklenen, tamamen ücretsiz bir uygulama.

⏰ GÜNLÜK SENKRON TEST
Her gün saat 09:00'da yayınlanan 12 soruluk teste katıl, tüm Türkiye ile aynı anda yarış ve günlük sıralamadaki yerini gör.

⚔️ 1v1 ARENA
Kullanıcı adıyla bir arkadaşına meydan oku ya da rastgele bir rakiple eşleş. Aynı soruları aynı anda çözün, canlı skor takibiyle kim daha hızlı ve doğru bakalım!

🎓 DENEME SINAVI
Gerçek KPSS formatına uygun 120 soru, 145 dakika. Sınav gününe kendine güvenerek gir.

📇 FLASH KARTLAR
Türkçe, Matematik, Tarih, Coğrafya, Vatandaşlık ve Güncel Bilgiler kategorilerinde hızlı tekrar yap.

🏆 CANLI LİDERLİK TABLOSU
Günlük ve haftalık sıralamalarda yerini gör, puanını artırdıkça zirveye tırman.

❌ YANLIŞLARIM
Yaptığın hataları kategoriye göre filtrele, açıklamalarıyla incele ve tekrar çözerek eksiklerini kapat.

📈 GELİŞİM TAKİBİ
Net skor grafiğin, test sayın, en yüksek skorun, maç galibiyetlerin ve serin tek ekranda.

🏆 BAŞARI AKIŞI
Test sonuçlarını, maç zaferlerini ve seri rekorlarını arkadaşlarınla paylaş, onların başarılarını beğen.

🔐 Google hesabınla saniyeler içinde giriş yap.
🌐 Türkçe ve İngilizce dil desteği.

KPSS Arena ile her gün biraz daha hazır, biraz daha rekabetçi ol. Zirveye çık!
```

## Full Description — EN (1377/4000 characters)

```
KPSS Arena: a question bank, daily tests, and mock exams in one app — preparing for KPSS doesn't have to be lonely, make it competitive and fun!

Compete with candidates from all over Turkey at the same time, track your progress, and challenge your friends. Ad-supported and completely free.

⏰ DAILY SYNCHRONIZED TEST
Join the 12-question test published every day at 09:00 and race against all of Turkey at once.

⚔️ 1v1 ARENA
Challenge a friend by username or get matched with a random opponent. Answer the same questions at the same time with live score tracking.

🎓 MOCK EXAM
120 questions, 145 minutes — matching the real KPSS format, so you walk in confident.

📇 FLASHCARDS
Quick review by category: Turkish, Math, History, Geography, Civics, and Current Affairs.

🏆 LIVE LEADERBOARD
See where you stand in the daily and weekly rankings and climb as your score grows.

❌ MY MISTAKES
Filter your past mistakes by category, review the explanations, and retry them until they're fixed.

📈 PROGRESS TRACKING
Your score chart, test count, highest score, match wins, and streak — all on one screen.

🏆 ACHIEVEMENT FEED
Share your test results, match wins, and streak milestones with friends, and like theirs back.

🔐 Sign in in seconds with your Google account.
🌐 Available in Turkish and English.

Get a little more ready, a little more competitive, every day with KPSS Arena.
```

## Diğer mağaza bilgileri (kodda doğrulandı)

- **Kategori:** Eğitim (Education)
- **Gizlilik Politikası URL:** `https://cynorra.github.io/kpss-arena-mobil/privacy.html` (GitHub Pages, `docs/privacy.html` kaynağından — TR/EN. Eskiden `cynorra.com/privacy` kullanılıyordu ama o alan adı hiç DNS kaydı olmadığı için ölüydü; 2026-09-16'da düzeltildi, `SettingsFragment` ve bu sayfa GitHub Pages URL'ine güncellendi.)
- **Reklam içerir:** Evet — manifest'te `AD_ID` izni var (AdMob), Play Console'da "İçerik reklam içeriyor" kutusu işaretlenmeli. UMP rıza formu artık `MainActivity` açılışında gerçekten çağrılıyor (2026-09-16'da düzeltildi — önceden `ConsentManager` hiç tetiklenmiyordu, EEA/UK kullanıcılarına rıza formu hiç gösterilmiyordu).
- **Uygulama içi satın alma:** Yok (Premium özelliği kod tabanından tamamen kaldırılmış)
- **Hesap Silme URL'i (Data safety → Account deletion):** `https://cynorra.github.io/kpss-arena-mobil/account-deletion.html` (GitHub Pages, `docs/account-deletion.html` kaynağından — TR/EN, uygulama içi silme + e-posta ile silme talebi anlatıyor). Uygulama içi silme (`Ayarlar → Hesabımı Sil`) artık hem Firestore profilini hem Auth kaydını gerçekten siliyor (2026-09-16'da düzeltildi — önceden sadece Auth kaydı siliniyordu, Firestore'da veri kalıcı olarak kalıyordu).
- **Destek e-postası:** `eren.gp54@gmail.com` (uygulama içinde `SettingsFragment`'ta zaten kullanılıyor — Play Console'a girerken bunu kullan)
- **Versiyon:** 1.0.1 (`versionCode` CI'da `GITHUB_RUN_NUMBER`'dan otomatik artıyor)
- **applicationId:** `com.cynorra.kpssarena`
- **App access (Play Console → App content):** Uygulamada kullanıcı adı/şifre yok, sadece Google ile giriş var (`FirebaseAuthSource.signInWithGoogle`), e-posta whitelist/kısıtlama da yok. Bu yüzden Play Console'da **"Tüm işlevler herkese açık, özel bir giriş bilgisine gerek yok"** seçeneği işaretlenmeli — ayrı bir test hesabı/şifre girilmesine gerek yok (Google incelemecisi kendi Google hesabıyla giriş yapabilir).
- **Uygulama türü (Oyun / Sosyal veya İletişim / Diğer Tüm Uygulama Türleri):** **Diğer Tüm Uygulama Türleri** — oyun değil (eğlence değil sınav hazırlığı amaçlı), asıl amacı da kullanıcılar arası iletişim değil (1v1 Arena ve liderlik tablosu yardımcı özellik, temel işlev soru bankası/test/deneme sınavı). Play Store kategorisi zaten "Eğitim" olarak seçili, tutarlı.

## Tamamlanmamış / manuel yapılması gerekenler

- **Uygulama simgesi (512×512) ve Feature graphic (1024×500):** hazır — `docs/store-assets/icon-512.png` ve `docs/store-assets/feature-1024x500.png` (marka renkleri/yıldız motifi `ic_launcher_foreground.xml` + `ic_launcher_background.xml`'den birebir alınarak Python/Pillow ile üretildi, 2026-09-16). Play Console'a doğrudan yüklenebilir.
- **Telefon ekran görüntüleri (min 2, tavsiye 4-8):** 7/8 hazır — `docs/store-assets/screenshots/01-ana-sayfa.jpg` ... `07-gelisim-takibi.jpg`. Gerçek cihaz/emülatör çıktısı alınamadı (yerel `Medium_Phone` AVD'sinin sistem imajı SDK'da hiç indirilmemiş, emülatör "Broken AVD system path" hatasıyla açılmadı — bkz. altta not), bunun yerine Gemini ile üretilip koddaki gerçek `strings.xml` metinleriyle birebir eşleşecek şekilde prompt'landı, sonra AI'nin bozduğu metin/etiket hataları (yanlış dil, tekrarlayan satırlar, anlamsız karakterler) `_work/fix_*.py` script'leriyle piksel bazında düzeltildi (2026-09-16). Eksik olan tek ekran: **#8 Başarı Akışı / paylaşım ekranı** — henüz üretilmedi.
- **Emülatör hâlâ çalışmıyor:** `system-images/android-36/google_apis_playstore/x86_64` klasörü SDK'da yok; indirmeye çalışırken C: sürücüsünde **0 byte boş alan** olduğu ortaya çıktı (asıl engel bu, "az bellek" sanılan sorunun gerçek nedeni muhtemelen buydu). `C:\Users\erens\Downloads` tek başına 12+ GB. Disk boşaltılırsa emülatör kurulup gerçek cihaz ekran görüntüsü de alınabilir.
- **İçerik derecelendirmesi (IARC) anketi** — Play Console'da doldurulması gereken ayrı bir form
- **Veri Güvenliği (Data safety) formu** — Play Console'da ayrı doldurulacak form: toplanan veriler (Firebase Auth, Firestore profil/skor verisi, AdMob reklam kimliği, Crashlytics) ve yukarıdaki hesap silme URL'i buraya işlenmeli
- **Tags (en fazla 5)** — Play Console'da Store presence → Main store listing → Tags, sabit bir taksonomiden seçiliyor, kod/dokümanla ayarlanamıyor ([Play Console Help](https://support.google.com/googleplay/android-developer/answer/9859673?hl=en)). Kodda doğrulanmış özelliklere göre öncelik sırası: **Quiz/Trivia** (soru-cevap çekirdek mekanik), **Education** (kategoriyle örtüşüyor ama ayrıca tag olarak da öneriliyor olabilir), **Flashcards/Study** (Flash Kartlar ekranı), **Exam prep** (Deneme Sınavı — KPSS formatı), **Multiplayer** (1v1 Arena). Console açıldığında önerilen tag listesi kategoriye göre değişir; yukarıdaki 5 kavramdan Console'da tam eşleşen adları seç.

## Ekran Görüntüsü Başlık Metin Planı (screenshot'lar henüz çekilmedi — çekilince overlay olarak kullan)

Play'in arama/keşif algoritması ekran görüntülerindeki metni de okuyabiliyor (OCR + context), o yüzden her ekranın üstüne kısa bir başlık koymak hem kullanıcıya hem SEO'ya fayda sağlıyor. Önerilen 6-8 ekran ve başlıkları (özellikler koddan doğrulandı):

| # | Ekran | Başlık — TR | Başlık — EN |
|---|-------|-------------|-------------|
| 1 | Ana sayfa / günlük test | Her gün saat 09:00 — tüm Türkiye ile aynı anda yarış | Every day at 9 AM — race all of Turkey at once |
| 2 | 1v1 Arena | Arkadaşını meydan oku, canlı skorla yarış | Challenge a friend, race live |
| 3 | Deneme Sınavı | Gerçek KPSS formatı: 120 soru, 145 dakika | Real KPSS format: 120 questions, 145 minutes |
| 4 | Flash Kartlar | 6 kategoride hızlı tekrar | Quick review across 6 categories |
| 5 | Liderlik Tablosu | Günlük ve haftalık sıralamada zirveye çık | Climb the daily & weekly leaderboard |
| 6 | Yanlışlarım | Hatalarını filtrele, açıklamasıyla tekrar çöz | Filter your mistakes, retry with explanations |
| 7 | Gelişim Takibi | Net skorun, serin, tüm ilerlemen tek ekranda | Your score, streak, and progress on one screen |
| 8 | Başarı Akışı | Zaferlerini paylaş, arkadaşlarını beğen | Share your wins, like your friends' |

## Not: fark edilen ayrı bir eksik (bu görevin kapsamı dışında, düzeltilmedi)

`UserProfileFragment`'taki "⚔️ Meydan Oku" butonu `toast_challenge_coming_soon` gösteriyor ve dekoratif; oysa Arena ekranındaki kullanıcı adıyla meydan okuma özelliği (`ArenaViewModel.challengeUser`) zaten tam çalışır durumda. Aynı akışı buraya bağlamak küçük bir iş olurdu — ayrı bir görev olarak ele alınabilir.
