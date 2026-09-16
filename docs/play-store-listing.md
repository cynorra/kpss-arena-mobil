# Play Store Mağaza Girişi (Store Listing)

Hazırlanma tarihi: 2026-09-14, son güncelleme: 2026-09-16. Uygulama sürümü: 1.0.0 (`app/build.gradle`).
Kaynak: `app/src/main/res/values/strings.xml`, `values-en/strings.xml`, `arrays.xml` — uydurma özellik yok, hepsi koddan doğrulandı.

## Başlık (30 karakter sınırı)

```
KPSS Arena
```

## Kısa Açıklama — TR (73/80 karakter)

```
Günlük test, 1v1 Arena ve deneme sınavlarıyla KPSS'ye eğlenerek hazırlan!
```

## Short Description — EN (71/80 characters)

```
Prep for KPSS with daily tests, 1v1 battles, mock exams & leaderboards!
```

## Tam Açıklama — TR (1404/4000 karakter)

```
KPSS Arena ile sınava hazırlanmak artık yalnız değil, rekabetçi ve eğlenceli!

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

## Full Description — EN (1315/4000 characters)

```
Preparing for KPSS doesn't have to be lonely — make it competitive and fun!

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
- **Gizlilik Politikası URL:** `https://cynorra.com/privacy` (uygulama içinde Ayarlar ekranında zaten kullanılıyor — Play Console'a girmeden önce içeriğin güncel olduğunu kontrol edin)
- **Reklam içerir:** Evet — manifest'te `AD_ID` izni var (AdMob), Play Console'da "İçerik reklam içeriyor" kutusu işaretlenmeli. UMP rıza formu artık `MainActivity` açılışında gerçekten çağrılıyor (2026-09-16'da düzeltildi — önceden `ConsentManager` hiç tetiklenmiyordu, EEA/UK kullanıcılarına rıza formu hiç gösterilmiyordu).
- **Uygulama içi satın alma:** Yok (Premium özelliği kod tabanından tamamen kaldırılmış)
- **Hesap Silme URL'i (Data safety → Account deletion):** `https://cynorra.github.io/kpss-arena-mobil/account-deletion.html` (GitHub Pages, `docs/account-deletion.html` kaynağından — TR/EN, uygulama içi silme + e-posta ile silme talebi anlatıyor). Uygulama içi silme (`Ayarlar → Hesabımı Sil`) artık hem Firestore profilini hem Auth kaydını gerçekten siliyor (2026-09-16'da düzeltildi — önceden sadece Auth kaydı siliniyordu, Firestore'da veri kalıcı olarak kalıyordu).
- **Destek e-postası:** `support@cynorra.com` (uygulama içinde `SettingsFragment`'ta zaten kullanılıyor — Play Console'a girerken bunu kullan)
- **Versiyon:** 1.0.0 (`versionCode` CI'da `GITHUB_RUN_NUMBER`'dan otomatik artıyor)
- **applicationId:** `com.cynorra.kpssarena`

## Tamamlanmamış / manuel yapılması gerekenler

- **Ekran görüntüleri** (min 2, tavsiye 4-8) ve **feature graphic (1024×500)** — gerçek cihaz/emülatör çıktısı olmalı, otomatik üretilemez
- **İçerik derecelendirmesi (IARC) anketi** — Play Console'da doldurulması gereken ayrı bir form
- **Veri Güvenliği (Data safety) formu** — Play Console'da ayrı doldurulacak form: toplanan veriler (Firebase Auth, Firestore profil/skor verisi, AdMob reklam kimliği, Crashlytics) ve yukarıdaki hesap silme URL'i buraya işlenmeli

## Not: fark edilen ayrı bir eksik (bu görevin kapsamı dışında, düzeltilmedi)

`UserProfileFragment`'taki "⚔️ Meydan Oku" butonu `toast_challenge_coming_soon` gösteriyor ve dekoratif; oysa Arena ekranındaki kullanıcı adıyla meydan okuma özelliği (`ArenaViewModel.challengeUser`) zaten tam çalışır durumda. Aynı akışı buraya bağlamak küçük bir iş olurdu — ayrı bir görev olarak ele alınabilir.
