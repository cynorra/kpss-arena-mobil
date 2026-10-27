// docs/sorular2.json uretici: soru verisi (kategori basina) + dogrulama + sik dengeleme.
// Kullanim: node docs/build_sorular2.js   ->  docs/sorular2.json
// Her sorunun "id" alani sabit (uuid) tutulur ki uploadQuestions.js ayni dosyayla tekrar calisirsa kopya olusmasin.
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// [metin, [secenekler], dogruIndex, konu, aciklama, siralidir(sayisal secenekler artan kalir)]
const TURKCE = [
  [`Aşağıdaki cümlelerin hangisinde "yüz" sözcüğü "surat" anlamında kullanılmıştır?`,
    [`Sınıfta yüz öğrenci vardı.`, `Yüzünü yıkayıp odadan çıktı.`, `Havuzda yüz metre yüzdü.`, `Yüz yıl önce burada küçük bir köy vardı.`], 1,
    `Sözcükte anlam`, `"Yüzünü yıkayıp" ifadesinde "yüz", insanın yüzü (surat) anlamındadır; diğerlerinde sayı veya fiil olarak kullanılmıştır.`],
  [`"Tasa" sözcüğünün eş anlamlısı aşağıdakilerden hangisidir?`,
    [`Sevinç`, `Kaygı`, `Öfke`, `Özlem`], 1, `Sözcükte anlam`, `"Tasa", kaygı ve endişe anlamına gelir.`],
  [`"Girift" sözcüğünün zıt anlamlısı aşağıdakilerden hangisidir?`,
    [`Karmaşık`, `Zor`, `Sade`, `Derin`], 2, `Sözcükte anlam`, `"Girift", karmaşık demektir; zıt anlamlısı sade ve basit olandır.`],
  [`"Ağzı kulaklarına varmak" deyimi aşağıdakilerden hangisini anlatır?`,
    [`Çok yemek yemek`, `Çok sevinmek`, `Çok konuşmak`, `Utanmak`], 1, `Deyimler`, `Bu deyim, yüzünden belli olacak kadar çok sevinmek anlamında kullanılır.`],
  [`"Kulak asmamak" deyimiyle anlamca en yakın ifade aşağıdakilerden hangisidir?`,
    [`Dikkatle dinlemek`, `Önemsememek`, `Şikâyet etmek`, `Hatırlamak`], 1, `Deyimler`, `"Kulak asmamak", söylenene önem vermemek, dikkate almamak demektir.`],
  [`"Damlaya damlaya göl olur." atasözünün ana fikri aşağıdakilerden hangisidir?`,
    [`Su israf edilmemelidir.`, `Küçük birikimler zamanla büyük bir değere ulaşır.`, `Her iş zamanında yapılmalıdır.`, `Kazanç paylaşıldıkça artar.`], 1,
    `Atasözleri`, `Atasözü, küçük birikimlerin zamanla büyük sonuçlar doğurduğunu anlatır.`],
  [`Aşağıdaki cümlelerin hangisinde neden-sonuç ilişkisi vardır?`,
    [`Yağmur yağdığı için maç ertelendi.`, `Ahmet hem çalışkan hem de dürüst bir öğrenciydi.`, `Dün akşam arkadaşlarımla sinemaya gittik.`, `Ya şimdi gel ya da hiç gelme.`], 0,
    `Cümlede anlam`, `İlk cümlede "yağmur yağması" neden, "maçın ertelenmesi" sonuçtur.`],
  [`"Sınavı kazanmak için gece gündüz çalıştı." cümlesinde aşağıdaki anlam ilişkilerinden hangisi vardır?`,
    [`Neden-sonuç`, `Amaç-sonuç`, `Koşul-sonuç`, `Karşılaştırma`], 1, `Cümlede anlam`, `"-mek için" kalıbı amaç bildirir; sınavı kazanmak amaç, çalışmak ise bu amaç için yapılan eylemdir.`],
  [`Aşağıdaki cümlelerin hangisinde koşul (şart) anlamı vardır?`,
    [`Erken kalkarsan treni kaçırmazsın.`, `Treni kaçırdığı için çok üzüldü.`, `Ders çalışmak için kütüphaneye gitti.`, `Kitabı alıp odadan çıktı.`], 0, `Cümlede anlam`, `"-sa/-se" eki ve sonucun ona bağlanması koşul bildirir.`],
  [`"Kitap okumak, yalnızca bilgi edinmenin değil aynı zamanda hayal gücünü geliştirmenin de yoludur. Okuyan kişi başkalarının gözüyle dünyayı görür; böylece kendi dünyasını genişletir." parçasının ana düşüncesi aşağıdakilerden hangisidir?`,
    [`Hayal gücü yalnızca kitapla gelişir.`, `Kitap okumak kişinin bilgisini ve bakış açısını zenginleştirir.`, `Herkes her gün kitap okumalıdır.`, `Dünyayı görmek için seyahat etmek gerekir.`], 1,
    `Paragraf`, `Parça, okumanın hem bilgi hem hayal gücü kazandırdığını ve kişinin dünyasını genişlettiğini anlatır.`],
  [`"Dil, bir toplumun ortak hafızasıdır. Söz varlığı, o toplumun yaşadıkları, inandıkları ve değer verdikleri şeylerle biçimlenir. Bu yüzden bir dili kaybetmek yalnızca iletişim aracını değil, bir kültür birikimini de yitirmek demektir." Bu parçadan aşağıdakilerden hangisi çıkarılamaz?`,
    [`Dil, kültürün taşıyıcısıdır.`, `Söz varlığı toplumun yaşantısıyla ilişkilidir.`, `Dilini yitiren toplum kültür birikimini de kaybeder.`, `Her dil eşit sayıda sözcüğe sahiptir.`], 3,
    `Paragraf`, `Parçada dillerin sözcük sayısıyla ilgili bir bilgi yoktur; diğer seçenekler parçadan çıkarılabilir.`],
  [`I. Bu nedenle sağlıklı beslenmek için öğünleri düzenli tutmak gerekir.\nII. Vücut, ihtiyaç duyduğu enerjiyi yediğimiz besinlerden alır.\nIII. Öğün atlamak, enerji dengesini bozar.\nIV. Dengesiz enerji ise yorgunluğa ve dikkat dağınıklığına yol açar.\n\nYukarıdaki cümlelerle anlamlı bir paragraf oluşturulmak istenirse cümleler hangi sırayla dizilmelidir?`,
    [`II - III - IV - I`, `I - II - III - IV`, `II - I - III - IV`, `III - II - IV - I`], 0, `Paragraf`, `Önce enerji kaynağı (II), sonra öğün atlamanın etkisi (III), onun sonucu (IV) ve çıkarım (I) gelir; "Bu nedenle" ile başlayan cümle ilk sırada olamaz.`],
  [`Aşağıdaki sözcüklerden hangisi ek aldığında ünsüz yumuşamasına uğrar?`,
    [`Ağaç`, `Kalem`, `Ev`, `Okul`], 0, `Ses bilgisi`, `"Ağaç" + "-ı" → "ağacı" (ç → c). Diğer sözcüklerde ünsüz yumuşaması olmaz.`],
  [`Aşağıdaki sözcüklerden hangisi ek alınca ünlü düşmesine uğrar?`,
    [`Omuz`, `El`, `Baş`, `Söz`], 0, `Ses bilgisi`, `"Omuz" + "-u" → "omzu"; ikinci hecedeki dar ünlü düşer.`],
  [`Aşağıdaki sözcüklerin hangisinin yazımı doğrudur?`,
    [`Herkez`, `Yanlız`, `Yalnız`, `Birşey`], 2, `Yazım kuralları`, `Doğru yazımlar "herkes", "yalnız" ve "bir şey"dir.`],
  [`Aşağıdaki cümlelerden hangisinde bir yazım yanlışı yapılmıştır?`,
    [`Akşamki toplantıya geç kaldı.`, `Herkes, hiç bir şey demeden dışarı çıktı.`, `Bu işi yarına kadar bitirmeliyiz.`, `Ankara'ya gidecektim, ama vazgeçtim.`], 1, `Yazım kuralları`, `"Hiçbir" sözcüğü bitişik yazılır.`],
  [`Aşağıdaki cümlelerin hangisinde noktalama yanlışı vardır?`,
    [`Ne yapacağını bilemedi, çaresiz kaldı.`, `"Yarın gelecek misin?" diye sordu.`, `Ankara İstanbul ve İzmir büyük şehirlerimizdir.`, `Dedi ki: "Sabırlı olmalıyız."`], 2, `Noktalama`, `Sıralı sözcükler arasında virgül kullanılır: "Ankara, İstanbul ve İzmir".`],
  [`Aşağıdaki cümlelerin hangisinde "güzel" sözcüğü isim olarak kullanılmıştır?`,
    [`Güzel çiçekler aldı.`, `Güzeller arasında en parlak olanı oydu.`, `Güzel konuşuyor.`, `Bugün hava güzel.`], 1, `Sözcük türleri`, `"Güzeller" çoğul ekiyle adlaşmış, isim olarak kullanılmıştır.`],
  [`Aşağıdaki cümlelerin hangisinde "hızlı" sözcüğü zarf (belirteç) olarak kullanılmıştır?`,
    [`Hızlı bir araba aldı.`, `Hızlı yürüdü, otobüse yetişti.`, `Hızlı çocuklar sevilir.`, `Hızlı adımlarla yaklaştı.`], 1, `Sözcük türleri`, `"Hızlı yürüdü" ifadesinde sözcük fiili niteler; diğerlerinde isim niteleyen sıfattır.`],
  [`"Yarın erken kalkacağım." cümlesindeki yüklemin kipi aşağıdakilerden hangisidir?`,
    [`Geniş zaman`, `Gelecek zaman`, `Şimdiki zaman`, `Görülen geçmiş zaman`], 1, `Fiiller`, `"-acak" eki gelecek zaman kipini oluşturur.`],
  [`Aşağıdaki cümlelerin hangisinin yüklemi edilgen çatılıdır?`,
    [`Kapıyı yavaşça açtı.`, `Mektup dün akşam yazıldı.`, `Çocuk bahçede güldü.`, `Havuzda uzun süre yüzdü.`], 1, `Fiillerde çatı`, `"Yazıldı" fiilinde "-ıl" eki vardır; iş, yapıcısı belirtilmeden nesnenin durumuna yöneltilmiştir.`],
  [`Aşağıdaki sözcüklerden hangisi yapım eki almıştır?`,
    [`Evler`, `Gözlük`, `Kitabı`, `Okudum`], 1, `Ekler`, `"Göz + -lük" yapım ekiyle yeni bir sözcük (gözlük) türetmiştir; diğerlerinde çekim ekleri vardır.`],
  [`Aşağıdakilerden hangisi isim tamlamasıdır?`,
    [`Mavi gömlek`, `Okulun bahçesi`, `Güzel manzara`, `Yüksek dağ`], 1, `Tamlamalar`, `"Okulun bahçesi" ad durum ekli, ilgi ve ilgili sözcükten oluşan bir isim tamlamasıdır; diğerleri sıfat tamlamasıdır.`],
  [`"Dün akşam arkadaşımla sinemaya gittik." cümlesinde "sinemaya" sözcüğü hangi ögedir?`,
    [`Özne`, `Nesne`, `Dolaylı tümleç`, `Zarf tümleci`], 2, `Cümlenin ögeleri`, `Yönelme hâli ekini alan ve yüklemin yönünü bildiren sözcük dolaylı tümleçtir.`],
  [`Aşağıdaki cümlelerin hangisinde gereksiz sözcük kullanımından kaynaklanan bir anlatım bozukluğu vardır?`,
    [`Yarın akşam bizde misafir olacaksınız.`, `Bu işi geri tekrar yapmak zorunda kaldı.`, `Kitabı masanın üzerine bıraktı.`, `Sabah erkenden yola çıktık.`], 1, `Anlatım bozuklukları`, `"Geri" ve "tekrar" aynı anlamı karşıladığından gereksiz sözcük kullanılmıştır.`],
  [`"Bu akşam hava çok güzel." cümlesi yüklemine göre aşağıdakilerden hangisidir?`,
    [`Fiil cümlesi`, `İsim cümlesi`, `Sıralı cümle`, `Birleşik cümle`], 1, `Cümle türleri`, `Yüklemi ("güzel") isim soylu bir sözcük olduğundan isim cümlesidir.`],
  [`Aşağıdaki cümlelerin hangisinde abartma vardır?`,
    [`Sınavdan sonra kimseyle konuşmadı.`, `Bu çantayı taşırken kollarım koptu.`, `Yarın erkenden görüşürüz.`, `Kitabı okuyup bitirdi.`], 1, `Anlatım sanatları`, `Kolların kopması, durumu olduğundan fazla göstererek abartılmıştır.`],
  [`Aşağıdaki cümlelerin hangisinde kişileştirme (teşhis) sanatı vardır?`,
    [`Güneş dağların ardından yavaş yavaş batıyordu.`, `Ağaçlar rüzgârla fısıldaşıyordu.`, `Deniz bugün çok mavi.`, `Kuşlar uçup gitti.`], 1, `Anlatım sanatları`, `Ağaçlara insana özgü "fısıldaşma" eylemi yüklenmiştir.`],
  [`Ayşe, Banu'dan uzun; Banu, Ceren'den uzundur.\n\nBuna göre aşağıdakilerden hangisi kesinlikle doğrudur?`,
    [`Ceren en uzundur.`, `Ayşe en uzundur.`, `Banu en kısadır.`, `Ceren, Ayşe'den uzundur.`], 1, `Sözel mantık`, `Ayşe > Banu > Ceren sıralaması vardır; en uzun Ayşe'dir.`],
  [`Bir yarışmada Kaan, Leyla'dan önce; Mert, Kaan'dan önce; Leyla ise Nur'dan önce bitirmiştir.\n\nBuna göre yarışmayı son sırada kim bitirmiştir?`,
    [`Mert`, `Kaan`, `Leyla`, `Nur`], 3, `Sözel mantık`, `Sıralama Mert, Kaan, Leyla, Nur şeklindedir; son sırada Nur vardır.`],
  [`"Bütün öğretmenler okumayı sever. Bazı okumayı sevenler yazardır."\n\nBu önermelere göre aşağıdakilerden hangisi kesinlikle doğrudur?`,
    [`Bazı öğretmenler yazardır.`, `Hiçbir yazar öğretmen değildir.`, `Bütün yazarlar okumayı sever.`, `Okumayı sevmeyen öğretmen yoktur.`], 3, `Sözel mantık`, `Bütün öğretmenler okumayı sevdiğine göre okumayı sevmeyen öğretmen yoktur; diğerleri önermelerden kesin olarak çıkarılamaz.`],
  [`Kırmızı, mavi ve yeşil renkli üç kutuda elma, armut ve kiraz vardır ve her kutuda yalnızca bir çeşit meyve bulunmaktadır. Kırmızı kutuda elma yoktur. Mavi kutuda kiraz vardır.\n\nBuna göre yeşil kutuda hangi meyve vardır?`,
    [`Elma`, `Armut`, `Kiraz`, `Belirlenemez`], 0, `Sözel mantık`, `Mavi kutuda kiraz, kırmızıda elma olmadığına göre armut vardır; geriye kalan yeşil kutuda elma bulunur.`],
  [`"Mevcut" sözcüğünün anlamı aşağıdakilerden hangisidir?`,
    [`Gelecek`, `Var olan`, `Eski`, `Önemli`], 1, `Sözcükte anlam`, `"Mevcut", var olan, hâlen bulunan anlamındadır.`],
  [`Aşağıdaki cümlelerin hangisi bir öneri bildirir?`,
    [`Bence bu işi bugün bitirsek iyi olur.`, `Dün akşam çok yoruldum.`, `Bu şehir çok kalabalık.`, `Kitap masanın üstündedir.`], 0, `Cümlede anlam`, `"Bitirsek iyi olur" ifadesi bir öneri sunar.`],
  [`Aşağıdaki cümlelerin hangisinde öznel bir yargı vardır?`,
    [`Türkiye'nin başkenti Ankara'dır.`, `Su deniz seviyesinde 100 °C'de kaynar.`, `Bu film izlediğim en güzel filmdi.`, `Bir yıl on iki aydan oluşur.`], 2, `Cümlede anlam`, `"En güzel" ifadesi kişinin kendi değerlendirmesidir, kanıtlanamaz.`],
  [`Aşağıdaki cümlelerin hangisinde "çay" sözcüğü içecek anlamı dışında kullanılmıştır?`,
    [`Sabah kahvaltıda çay içti.`, `Köyün yanından küçük bir çay geçiyordu.`, `Çaya iki şeker attı.`, `Misafirlere taze çay demledi.`], 1, `Sözcükte anlam`, `Burada "çay", küçük akarsu anlamındadır.`],
  [`Aşağıdaki sözcüklerden hangisinde ünsüz benzeşmesi (sertleşme) vardır?`,
    [`Kitapçı`, `Kalemlik`, `Sevgi`, `Evlilik`], 0, `Ses bilgisi`, `"Kitap" sert ünsüzle bittiği için "-cı" eki "-çı" biçimine dönüşmüştür.`],
  [`Aşağıdaki cümlelerin hangisinde büyük harfle başlaması gereken sözcük küçük harfle yazılmıştır?`,
    [`Atatürk'ün izinden gidiyoruz.`, `Geçen hafta ankara'ya gittik.`, `Kitabı kardeşime verdim.`, `Dün akşam televizyon izledik.`], 1, `Yazım kuralları`, `Özel adlar ve onlara gelen ekler kesme işaretiyle ayrılır; "Ankara" büyük harfle başlamalıdır.`],
  [`"Bu konuda uzmanların söylediklerini dikkate almalısın." cümlesinde aşağıdaki anlamlardan hangisi vardır?`,
    [`Öğüt verme`, `Sitem`, `Şaşkınlık`, `Pişmanlık`], 0, `Cümlede anlam`, `"-malısın" kalıbıyla karşıdakine öğüt verilmektedir.`],
];

const MATEMATIK = [
  [`3x − 7 = 11 olduğuna göre x kaçtır?`, [`4`, `5`, `6`, `7`], 2, `Denklemler`, `3x = 18 olduğundan x = 6.`, 1],
  [`2/3 + 3/4 işleminin sonucu kaçtır?`, [`5/7`, `17/12`, `1`, `13/12`], 1, `Rasyonel sayılar`, `8/12 + 9/12 = 17/12.`, 0],
  [`12, 18 ve 30 sayılarının EBOB'u kaçtır?`, [`2`, `3`, `6`, `12`], 2, `EBOB - EKOK`, `Ortak bölenlerin en büyüğü 6'dır.`, 1],
  [`Ardışık üç tek sayının toplamı 63 olduğuna göre bu sayıların en büyüğü kaçtır?`, [`21`, `23`, `25`, `27`], 1, `Sayı problemleri`, `Sayılar 19, 21, 23'tür; toplamları 63 ve en büyüğü 23.`, 1],
  [`Bir sayının 3 katının 5 eksiği 28 olduğuna göre bu sayı kaçtır?`, [`9`, `10`, `11`, `12`], 2, `Sayı problemleri`, `3x − 5 = 28 → 3x = 33 → x = 11.`, 1],
  [`Bir ürün %25 indirimle 150 TL'ye satılmaktadır. Ürünün indirimsiz fiyatı kaç TL'dir?`, [`175`, `200`, `210`, `225`], 1, `Yüzde problemleri`, `Fiyatın %75'i 150 TL ise fiyat 150 / 0,75 = 200 TL'dir.`, 1],
  [`2000 TL anapara yıllık %10 basit faizle 3 yıl bankada kalırsa kaç TL faiz getirir?`, [`400`, `500`, `600`, `700`], 2, `Faiz problemleri`, `2000 × 0,10 × 3 = 600 TL.`, 1],
  [`Bir işi A tek başına 6 günde, B tek başına 3 günde bitirmektedir. İkisi birlikte çalışırsa işi kaç günde bitirirler?`, [`1`, `2`, `3`, `4`], 1, `İşçi problemleri`, `1/6 + 1/3 = 1/2 olduğundan birlikte 2 günde bitirirler.`, 1],
  [`Saatteki hızı 60 km olan bir araç 150 km'lik yolu kaç saatte alır?`, [`2 saat`, `2,5 saat`, `3 saat`, `3,5 saat`], 1, `Hareket problemleri`, `Süre = yol / hız = 150 / 60 = 2,5 saat.`, 1],
  [`5 sayının aritmetik ortalaması 12'dir. Bu sayılardan dördünün toplamı 44 olduğuna göre beşinci sayı kaçtır?`, [`14`, `15`, `16`, `18`], 2, `Ortalama`, `Toplam 5 × 12 = 60; beşinci sayı 60 − 44 = 16.`, 1],
  [`Bir sınıftaki 30 öğrencinin 12'si kızdır. Erkek öğrencilerin sınıfa oranı kaçtır?`, [`2/5`, `3/5`, `1/2`, `3/4`], 1, `Oran - Orantı`, `Erkek sayısı 18'dir; 18/30 = 3/5.`, 0],
  [`a/b = 3/4 ve a + b = 28 olduğuna göre a kaçtır?`, [`9`, `12`, `15`, `16`], 1, `Oran - Orantı`, `a = 3k, b = 4k; 7k = 28 → k = 4 ve a = 12.`, 1],
  [`x + y = 10 ve x − y = 4 olduğuna göre x · y çarpımı kaçtır?`, [`18`, `21`, `24`, `28`], 1, `Denklem sistemleri`, `Taraf tarafa toplanırsa x = 7, y = 3; çarpım 21.`, 1],
  [`2⁵ + 3² işleminin sonucu kaçtır?`, [`35`, `39`, `41`, `45`], 2, `Üslü sayılar`, `32 + 9 = 41.`, 1],
  [`√144 + √81 işleminin sonucu kaçtır?`, [`19`, `21`, `23`, `25`], 1, `Köklü sayılar`, `12 + 9 = 21.`, 1],
  [`Bir üçgenin iki iç açısı 50° ve 65° olduğuna göre üçüncü açısı kaç derecedir?`, [`55`, `60`, `65`, `70`], 2, `Üçgenler`, `İç açıların toplamı 180°; 180 − 115 = 65°.`, 1],
  [`Çevresi 36 cm olan karenin alanı kaç cm²'dir?`, [`72`, `81`, `84`, `96`], 1, `Geometri`, `Bir kenar 9 cm; alan 9 × 9 = 81 cm².`, 1],
  [`Kısa kenarı 5 cm, uzun kenarı 12 cm olan dikdörtgenin köşegen uzunluğu kaç cm'dir?`, [`11`, `12`, `13`, `17`], 2, `Geometri`, `Pisagor: √(25 + 144) = 13.`, 1],
  [`Yarıçapı 7 cm olan çemberin çevresi kaç cm'dir? (π = 22/7)`, [`22`, `44`, `88`, `154`], 1, `Geometri`, `2 · π · r = 2 · (22/7) · 7 = 44 cm.`, 1],
  [`Yarıçapı 3 cm olan dairenin alanı kaç cm²'dir? (π = 3 alınız)`, [`18`, `27`, `36`, `54`], 1, `Geometri`, `π · r² = 3 · 9 = 27 cm².`, 1],
  [`Bir kenarı 4 cm olan küpün hacmi kaç cm³'tür?`, [`16`, `48`, `64`, `96`], 2, `Geometri`, `4³ = 64 cm³.`, 1],
  [`Bir torbada 3 kırmızı ve 2 mavi top vardır. Torbadan rastgele çekilen bir topun mavi olma olasılığı kaçtır?`, [`1/5`, `2/5`, `3/5`, `1/2`], 1, `Olasılık`, `Mavi top sayısı 2, toplam top sayısı 5 olduğundan olasılık 2/5'tir.`, 0],
  [`Bir madeni para art arda iki kez atılıyor. İkisinin de yazı gelme olasılığı kaçtır?`, [`1/8`, `1/4`, `1/2`, `3/4`], 1, `Olasılık`, `(1/2) · (1/2) = 1/4.`, 0],
  [`Bir A kümesinin eleman sayısı 8, B kümesinin eleman sayısı 5 ve kesişimlerinin eleman sayısı 3'tür. Buna göre A ∪ B kümesinin eleman sayısı kaçtır?`, [`8`, `10`, `13`, `16`], 1, `Kümeler`, `s(A ∪ B) = 8 + 5 − 3 = 10.`, 1],
  [`4 kişi bir sıraya kaç farklı şekilde dizilebilir?`, [`12`, `16`, `24`, `48`], 2, `Permütasyon`, `4! = 24.`, 1],
  [`1 + 2 + 3 + ... + 10 toplamı kaçtır?`, [`45`, `50`, `55`, `66`], 2, `Diziler`, `10 · 11 / 2 = 55.`, 1],
  [`İlk terimi 3, ortak farkı 4 olan aritmetik dizinin 10. terimi kaçtır?`, [`35`, `39`, `43`, `47`], 1, `Diziler`, `a₁₀ = 3 + 9 · 4 = 39.`, 1],
  [`2, 6, 18, 54, ... şeklinde devam eden dizinin 5. terimi kaçtır?`, [`108`, `148`, `162`, `216`], 2, `Diziler`, `Her terim 3 ile çarpılır: 54 · 3 = 162.`, 1],
  [`f(x) = 2x + 3 olduğuna göre f(4) kaçtır?`, [`9`, `10`, `11`, `14`], 2, `Fonksiyonlar`, `f(4) = 2 · 4 + 3 = 11.`, 1],
  [`|x − 2| < 3 eşitsizliğini sağlayan kaç tam sayı vardır?`, [`4`, `5`, `6`, `7`], 1, `Mutlak değer`, `−1 < x < 5 olduğundan x = 0, 1, 2, 3, 4; toplam 5 tam sayı.`, 1],
  [`Bir sayının yarısının üçte biri 5 ise bu sayı kaçtır?`, [`20`, `25`, `30`, `45`], 2, `Sayı problemleri`, `x / 6 = 5 → x = 30.`, 1],
  [`Saat 3.30'da akrep ile yelkovan arasındaki küçük açı kaç derecedir?`, [`60`, `75`, `90`, `105`], 1, `Saat problemleri`, `Akrep 105°, yelkovan 180° konumundadır; aradaki açı 75°.`, 1],
  [`Annenin yaşı kızının yaşının 4 katıdır. 12 yıl sonra annenin yaşı kızının yaşının 2 katı olacaktır. Kızın bugünkü yaşı kaçtır?`, [`4`, `6`, `8`, `12`], 1, `Yaş problemleri`, `4x + 12 = 2(x + 12) → x = 6.`, 1],
  [`Aralarında 360 km mesafe bulunan iki şehirden, saatteki hızları 70 km ve 50 km olan iki araç aynı anda birbirine doğru yola çıkıyor. Kaç saat sonra karşılaşırlar?`, [`2`, `3`, `4`, `6`], 1, `Hareket problemleri`, `Yaklaşma hızı 120 km/saat; 360 / 120 = 3 saat.`, 1],
  [`7, 14, 28, 56, ... dizisinde sıradaki sayı kaçtır?`, [`84`, `98`, `112`, `120`], 2, `Sayısal mantık`, `Her terim 2 ile çarpılıyor: 56 · 2 = 112.`, 1],
  [`1, 4, 9, 16, 25, ... dizisinde sıradaki sayı kaçtır?`, [`30`, `34`, `36`, `49`], 2, `Sayısal mantık`, `Terimler 1², 2², 3², ... şeklindedir; sıradaki 6² = 36.`, 1],
  [`a * b = 2a + b işlemi tanımlanıyor. Buna göre 3 * 4 kaçtır?`, [`8`, `10`, `12`, `14`], 1, `İşlem`, `2 · 3 + 4 = 10.`, 1],
  [`2¹⁰ sayısının birler basamağındaki rakam kaçtır?`, [`2`, `4`, `6`, `8`], 1, `Sayısal mantık`, `2¹⁰ = 1024 olduğundan birler basamağı 4'tür.`, 1],
  [`Rakamları toplamı 9 olan en küçük iki basamaklı doğal sayı kaçtır?`, [`18`, `27`, `36`, `45`], 0, `Sayı problemleri`, `Birler basamağı 8, onlar basamağı 1 olan 18 sayısı en küçüğüdür.`, 1],
  [`Bir sayının %40'ı 36 ise bu sayı kaçtır?`, [`80`, `90`, `100`, `120`], 1, `Yüzde problemleri`, `36 / 0,40 = 90.`, 1],
  [`72 sayısının asal çarpanlarının toplamı kaçtır?`, [`5`, `6`, `8`, `13`], 0, `Asal çarpanlar`, `72 = 2³ · 3² olduğundan asal çarpanlar 2 ve 3; toplam 5.`, 1],
  [`0,25 × 0,4 işleminin sonucu kaçtır?`, [`0,01`, `0,1`, `1`, `10`], 1, `Ondalık sayılar`, `25 × 4 = 100 ve virgülden sonra üç basamak olduğundan sonuç 0,100 = 0,1'dir.`, 1],
  [`4, 6, 6, 8, 10 veri grubunun ortancası ile tepe değerinin (modunun) toplamı kaçtır?`, [`10`, `12`, `14`, `16`], 1, `İstatistik`, `Ortanca 6, mod 6 olduğundan toplam 12.`, 1],
  [`Fiyatı 100 TL olan bir ürüne önce %10 zam, ardından yeni fiyata %10 indirim yapılıyor. Ürünün son fiyatı kaç TL olur?`, [`95`, `99`, `100`, `101`], 1, `Yüzde problemleri`, `100 · 1,10 = 110; 110 · 0,90 = 99 TL.`, 1],
];

const TARIH = [
  [`Orhun Yazıtları aşağıdaki alfabelerden hangisiyle yazılmıştır?`, [`Uygur alfabesi`, `Göktürk alfabesi`, `Arap alfabesi`, `Kiril alfabesi`], 1, `İlk Türk devletleri`, `Orhun (Göktürk) Yazıtları, Göktürk alfabesiyle yazılmıştır.`],
  [`İslamiyet öncesi Türk devletlerinde hükümdarın yönetme yetkisinin Tanrı tarafından verildiği inancına ne ad verilir?`, [`Töre`, `Kut`, `Yarlığ`, `Tuğ`], 1, `İlk Türk devletleri`, `Hükümdarın yönetme gücünün Tanrı'dan geldiği inanışına "kut" denir.`],
  [`Tarihte "Türk" adını devlet adı olarak kullanan ilk Türk devleti aşağıdakilerden hangisidir?`, [`Hunlar`, `Göktürkler`, `Uygurlar`, `Karahanlılar`], 1, `İlk Türk devletleri`, `"Türk" adı ilk kez Göktürk Devleti'nde devlet adı olarak kullanılmıştır.`],
  [`Türk tarihinde orduyu ilk kez onluk sisteme göre örgütleyen hükümdar aşağıdakilerden hangisidir?`, [`Mete Han`, `Bumin Kağan`, `Bilge Kağan`, `Attila`], 0, `İlk Türk devletleri`, `Asya Hun hükümdarı Mete Han orduyu onlu sistemle düzenlemiştir.`],
  [`Aşağıdaki Türk devletlerinden hangisi ilk kez yerleşik hayata geçerek şehirler kurmuştur?`, [`Hunlar`, `Göktürkler`, `Uygurlar`, `Avarlar`], 2, `İlk Türk devletleri`, `Uygurlar tarım yapıp şehirler kurarak yerleşik hayata geçmiştir.`],
  [`751 Talas Savaşı'nın sonuçlarından biri aşağıdakilerden hangisidir?`, [`Türklerin İslamiyet'i kabul sürecinin hızlanması`, `Selçuklu Devleti'nin kurulması`, `Haçlı Seferleri'nin başlaması`, `Büyük Hun Devleti'nin yıkılması`], 0, `İslamiyet'in kabulü`, `Talas Savaşı sonrası Türkler ile Müslüman Araplar arasındaki yakınlaşma İslamiyet'in yayılmasını hızlandırmıştır.`],
  [`İslamiyet'i resmî din olarak kabul eden ilk Türk devleti aşağıdakilerden hangisidir?`, [`Gazneliler`, `Karahanlılar`, `Büyük Selçuklular`, `Harzemşahlar`], 1, `İslamiyet'in kabulü`, `Karahanlılar, Satuk Buğra Han döneminde İslamiyet'i resmî din olarak kabul etmiştir.`],
  [`"Sultan" unvanını kullanan ilk Türk hükümdarı aşağıdakilerden hangisidir?`, [`Gazneli Mahmud`, `Tuğrul Bey`, `Alp Arslan`, `Yusuf Has Hacip`], 0, `Türk-İslam devletleri`, `Gazneli Mahmud, "sultan" unvanını kullanan ilk Türk hükümdarıdır.`],
  [`Türkçenin ilk sözlüğü ve gramer kitabı sayılan "Divanü Lügati't-Türk" kime aittir?`, [`Yusuf Has Hacip`, `Kaşgarlı Mahmud`, `Ahmet Yesevi`, `Edip Ahmet`], 1, `Türk-İslam kültürü`, `Divanü Lügati't-Türk'ü Kaşgarlı Mahmud yazmıştır.`],
  [`Malazgirt Savaşı'nda Bizans ordusunu yenen Selçuklu sultanı aşağıdakilerden hangisidir?`, [`Tuğrul Bey`, `Alp Arslan`, `Melikşah`, `Sencer`], 1, `Selçuklular`, `1071'de Alp Arslan, Romen Diyojen komutasındaki Bizans ordusunu yenmiştir.`],
  [`Nizamiye Medreseleri'ni kuran Selçuklu veziri aşağıdakilerden hangisidir?`, [`Nizamülmülk`, `Gazali`, `Ömer Hayyam`, `Hasan Sabbah`], 0, `Selçuklular`, `Nizamiye Medreseleri, Büyük Selçuklu veziri Nizamülmülk tarafından kurulmuştur.`],
  [`Anadolu Selçuklu Devleti'nin kurucusu aşağıdakilerden hangisidir?`, [`Kutalmışoğlu Süleymanşah`, `Alp Arslan`, `II. Kılıçarslan`, `I. Alaeddin Keykubad`], 0, `Anadolu Selçukluları`, `Anadolu Selçuklu Devleti, Kutalmışoğlu Süleymanşah tarafından kurulmuştur.`],
  [`Anadolu'nun Türk yurdu olmasını kesinleştiren savaş aşağıdakilerden hangisidir?`, [`Malazgirt`, `Miryokefalon`, `Kösedağ`, `Dandanakan`], 1, `Anadolu Selçukluları`, `1176 Miryokefalon Savaşı ile Bizans'ın Anadolu'yu geri alma umudu sona ermiştir.`],
  [`Anadolu Selçuklu Devleti hangi savaştan sonra Moğol egemenliği altına girmiştir?`, [`Malazgirt`, `Miryokefalon`, `Kösedağ`, `Ankara`], 2, `Anadolu Selçukluları`, `1243 Kösedağ Savaşı'ndan sonra Anadolu Selçuklular Moğollara bağlanmıştır.`],
  [`İstanbul'un fethi (1453) hangi dönemin sonu kabul edilir?`, [`Orta Çağ'ın sonu ve Yeni Çağ'ın başlangıcı`, `Yeni Çağ'ın sonu`, `İlk Çağ'ın sonu`, `Yakın Çağ'ın başlangıcı`], 0, `Osmanlı Devleti (Kuruluş)`, `İstanbul'un fethi, Orta Çağ'ın sonu ve Yeni Çağ'ın başlangıcı olarak kabul edilir.`],
  [`Osmanlı'da kardeş katlini kanunlaştıran padişah aşağıdakilerden hangisidir?`, [`Yıldırım Bayezid`, `Fatih Sultan Mehmet`, `Kanuni Sultan Süleyman`, `II. Murat`], 1, `Osmanlı Devleti (Yükselme)`, `Fatih Sultan Mehmet, Kanunname ile devletin bölünmemesi için kardeş katlini kanunlaştırmıştır.`],
  [`Mercidabık (1516) ve Ridaniye (1517) savaşlarıyla Osmanlı Devleti hangi devleti ortadan kaldırmıştır?`, [`Safeviler`, `Memlükler`, `Bizans`, `Eyyubiler`], 1, `Osmanlı Devleti (Yükselme)`, `Yavuz Sultan Selim bu savaşlarla Memlük Devleti'ne son vermiştir.`],
  [`Osmanlı donanmasının Akdeniz'de üstünlüğü ele geçirdiği 1538 deniz savaşı aşağıdakilerden hangisidir?`, [`İnebahtı`, `Preveze`, `Çaldıran`, `Mohaç`], 1, `Osmanlı Devleti (Yükselme)`, `Barbaros Hayrettin Paşa komutasındaki Osmanlı donanması Preveze Savaşı'nı kazanmıştır.`],
  [`Çaldıran Savaşı (1514) Osmanlı Devleti ile hangi devlet arasında yapılmıştır?`, [`Memlükler`, `Safeviler`, `Avusturya`, `Venedik`], 1, `Osmanlı Devleti (Yükselme)`, `Çaldıran'da Yavuz Sultan Selim, Safevi Şahı İsmail'i yenmiştir.`],
  [`Osmanlı Devleti'nin Avrupa devletleriyle eşit şartlarda imzaladığı ilk antlaşma aşağıdakilerden hangisidir?`, [`Karlofça`, `Zitvatorok`, `Küçük Kaynarca`, `Belgrad`], 1, `Osmanlı Devleti (Duraklama)`, `1606 Zitvatorok Antlaşması ile Osmanlı, Avusturya'yı eşit konuma getirmiştir.`],
  [`Osmanlı Devleti'nin ilk büyük toprak kaybına yol açan antlaşma aşağıdakilerden hangisidir?`, [`Karlofça`, `Pasarofça`, `Zitvatorok`, `Prut`], 0, `Osmanlı Devleti (Gerileme)`, `1699 Karlofça Antlaşması ile Osmanlı ilk kez büyük toprak kaybetmiştir.`],
  [`Osmanlı'da ilk Türkçe matbaayı kuran kişi aşağıdakilerden hangisidir?`, [`Sait Efendi`, `İbrahim Müteferrika`, `Evliya Çelebi`, `Kâtip Çelebi`], 1, `Lale Devri`, `İbrahim Müteferrika, Sait Efendi ile birlikte 1727'de ilk Türkçe matbaayı kurmuştur.`],
  [`III. Selim döneminde kurulan yeni askerî birliğin adı aşağıdakilerden hangisidir?`, [`Yeniçeri Ocağı`, `Nizam-ı Cedit`, `Asakir-i Mansure`, `Sipahi Ocağı`], 1, `Osmanlı Devleti (Yenileşme)`, `III. Selim, Avrupa tarzında Nizam-ı Cedit ordusunu kurmuştur.`],
  [`Yeniçeri Ocağı hangi padişah döneminde kaldırılmıştır?`, [`III. Selim`, `II. Mahmud`, `Abdülmecid`, `II. Abdülhamid`], 1, `Osmanlı Devleti (Yenileşme)`, `II. Mahmud, 1826'da Vaka-i Hayriye ile Yeniçeri Ocağı'nı kaldırmıştır.`],
  [`Osmanlı Devleti'nin Avrupa devletler hukukuna kabul edildiği antlaşma aşağıdakilerden hangisidir?`, [`Küçük Kaynarca`, `Paris`, `Berlin`, `Londra`], 1, `Osmanlı Devleti (Yenileşme)`, `1856 Paris Antlaşması ile Osmanlı Devleti Avrupa devletler hukukuna dâhil edilmiştir.`],
  [`Osmanlı Devleti'nin ilk anayasası aşağıdakilerden hangisidir?`, [`Sened-i İttifak`, `Tanzimat Fermanı`, `Kanun-ı Esasi`, `Islahat Fermanı`], 2, `Meşrutiyet`, `1876'da ilan edilen Kanun-ı Esasi, Osmanlı'nın ilk anayasasıdır.`],
  [`İkinci Meşrutiyet hangi yıl ilan edilmiştir?`, [`1876`, `1878`, `1908`, `1909`], 2, `Meşrutiyet`, `İttihat ve Terakki Cemiyeti'nin baskısıyla II. Meşrutiyet 1908'de ilan edilmiştir.`, 1],
  [`Çanakkale Savaşları'nda Anafartalar Grup Komutanı olarak kazanılan zaferde önemli rol oynayan komutan aşağıdakilerden hangisidir?`, [`Enver Paşa`, `Mustafa Kemal`, `Cemal Paşa`, `Liman von Sanders`], 1, `Birinci Dünya Savaşı`, `Mustafa Kemal, Anafartalar Grup Komutanı olarak Çanakkale'de başarı göstermiştir.`],
  [`Osmanlı Devleti'nin Birinci Dünya Savaşı'ndan çekildiğini gösteren ateşkes antlaşması aşağıdakilerden hangisidir?`, [`Mudanya`, `Mondros`, `Sevr`, `Lozan`], 1, `Millî Mücadele`, `30 Ekim 1918'de imzalanan Mondros Ateşkes Antlaşması ile Osmanlı savaştan çekilmiştir.`],
  [`TBMM Hükûmeti tarafından hiçbir zaman onaylanmayan ve uygulanmayan antlaşma aşağıdakilerden hangisidir?`, [`Mondros`, `Sevr`, `Ankara`, `Mudanya`], 1, `Millî Mücadele`, `1920 Sevr Antlaşması, TBMM tarafından tanınmamış ve uygulanmamıştır.`],
  [`Millî Mücadele'nin gerekçesi ve amacının ilk kez açıkça ortaya konduğu belge aşağıdakilerden hangisidir?`, [`Havza Genelgesi`, `Amasya Genelgesi`, `Erzurum Kongresi`, `Sivas Kongresi`], 1, `Millî Mücadele`, `22 Haziran 1919 tarihli Amasya Genelgesi'nde millî mücadelenin gerekçesi ve amacı belirtilmiştir.`],
  [`Misak-ı Millî hangi mecliste kabul edilmiştir?`, [`TBMM`, `Son Osmanlı Mebusan Meclisi`, `Sivas Kongresi`, `Erzurum Kongresi`], 1, `Millî Mücadele`, `Misak-ı Millî, 28 Ocak 1920'de Son Osmanlı Mebusan Meclisi'nde kabul edilmiştir.`],
  [`Sakarya Meydan Muharebesi'nden sonra Mustafa Kemal'e verilen unvan ve rütbe aşağıdakilerden hangisidir?`, [`Gazilik unvanı ve Mareşallik rütbesi`, `Başkomutanlık unvanı`, `Halifelik unvanı`, `Paşalık rütbesi`], 0, `Millî Mücadele`, `Sakarya Zaferi'nin ardından TBMM, Mustafa Kemal'e Gazilik unvanı ve Mareşallik rütbesi vermiştir.`],
  [`Başkomutan Meydan Muharebesi hangi tarihte yapılmıştır?`, [`26 Ağustos 1922`, `30 Ağustos 1922`, `9 Eylül 1922`, `11 Ekim 1922`], 1, `Millî Mücadele`, `Büyük Taarruz sonunda 30 Ağustos 1922'de Başkomutan Meydan Muharebesi kazanılmıştır.`, 1],
  [`Kurtuluş Savaşı'nın askerî safhasını sona erdiren belge aşağıdakilerden hangisidir?`, [`Mondros`, `Mudanya`, `Lozan`, `Ankara`], 1, `Millî Mücadele`, `11 Ekim 1922 Mudanya Ateşkesi ile savaş askerî yönden sona ermiştir.`],
  [`Cumhuriyet ilan edildikten sonra kurulan ilk hükûmetin Başbakanı aşağıdakilerden hangisidir?`, [`Fethi Okyar`, `İsmet İnönü`, `Rauf Orbay`, `Kazım Karabekir`], 1, `Atatürk Dönemi`, `29 Ekim 1923'te Cumhuriyet ilan edildiğinde ilk hükûmeti İsmet Paşa kurmuştur.`],
  [`3 Mart 1924'te kabul edilen Tevhid-i Tedrisat Kanunu ile aşağıdakilerden hangisi sağlanmıştır?`, [`Eğitim ve öğretimin birleştirilmesi`, `Harflerin değiştirilmesi`, `Kadınlara seçme hakkı verilmesi`, `Medeni Kanun'un kabulü`], 0, `Atatürk Dönemi`, `Tevhid-i Tedrisat ile tüm eğitim kurumları Millî Eğitim Bakanlığı'na bağlanmıştır.`],
  [`Yeni Türk harfleri hangi yıl kabul edilmiştir?`, [`1925`, `1926`, `1928`, `1934`], 2, `Atatürk Dönemi`, `Harf Devrimi 1928'de gerçekleştirilmiştir.`, 1],
  [`Aşağıdakilerden hangisi 1926'da kabul edilen Medeni Kanun'un esas alındığı ülkedir?`, [`Fransa`, `İtalya`, `İsviçre`, `Almanya`], 2, `Atatürk Dönemi`, `Türk Medeni Kanunu, İsviçre Medeni Kanunu'ndan alınmıştır.`],
  [`Kadınlara milletvekili seçme ve seçilme hakkı hangi yıl verilmiştir?`, [`1926`, `1930`, `1934`, `1945`], 2, `Atatürk Dönemi`, `Kadınlara milletvekili seçme ve seçilme hakkı 1934'te tanınmıştır.`, 1],
  [`Boğazlar üzerinde Türkiye'nin askerî egemenlik hakkını yeniden kazandığı sözleşme aşağıdakilerden hangisidir?`, [`Lozan`, `Montrö`, `Sevr`, `Mudanya`], 1, `Atatürk Dönemi`, `1936 Montrö Boğazlar Sözleşmesi ile Türkiye Boğazlar'da egemenlik elde etmiştir.`],
  [`Hatay hangi yıl Türkiye'ye katılmıştır?`, [`1923`, `1936`, `1939`, `1945`], 2, `Atatürk Dönemi`, `Hatay, 1939'da Türkiye'ye katılmıştır.`, 1],
  [`1950 seçimlerinde iktidara gelen siyasi parti aşağıdakilerden hangisidir?`, [`Cumhuriyet Halk Partisi`, `Demokrat Parti`, `Millet Partisi`, `Türkiye Komünist Partisi`], 1, `Çok partili dönem`, `1950 seçimlerinde Demokrat Parti iktidara gelmiştir.`],
  [`27 Mayıs 1960 askerî müdahalesinden sonra hazırlanan anayasa hangisidir?`, [`1924 Anayasası`, `1961 Anayasası`, `1982 Anayasası`, `1876 Kanun-ı Esasi`], 1, `Çok partili dönem`, `27 Mayıs sonrasında 1961 Anayasası hazırlanmıştır.`],
  [`Kıbrıs Barış Harekâtı hangi yıl yapılmıştır?`, [`1964`, `1971`, `1974`, `1980`], 2, `Çağdaş Türkiye`, `Kıbrıs Barış Harekâtı Temmuz 1974'te gerçekleştirilmiştir.`, 1],
];

const COGRAFYA = [
  [`Türkiye'nin yüz ölçümü yaklaşık kaç km²'dir?`, [`583 bin`, `683 bin`, `783 bin`, `883 bin`], 2, `Türkiye'nin coğrafi konumu`, `Türkiye'nin yüz ölçümü yaklaşık 783 bin km²'dir.`, 1],
  [`Türkiye'nin en büyük gölü aşağıdakilerden hangisidir?`, [`Tuz Gölü`, `Van Gölü`, `Beyşehir Gölü`, `Eğirdir Gölü`], 1, `Türkiye'nin yer şekilleri`, `Yüz ölçümü bakımından en büyük göl Van Gölü'dür.`],
  [`Türkiye'nin en büyük tatlı su gölü aşağıdakilerden hangisidir?`, [`Eğirdir Gölü`, `Beyşehir Gölü`, `İznik Gölü`, `Burdur Gölü`], 1, `Türkiye'nin yer şekilleri`, `En büyük tatlı su gölü Beyşehir Gölü'dür.`],
  [`Kendi sınırları içinde doğup denize ulaşan en uzun akarsuyumuz aşağıdakilerden hangisidir?`, [`Fırat`, `Kızılırmak`, `Sakarya`, `Yeşilırmak`], 1, `Türkiye'nin akarsuları`, `Kızılırmak, Türkiye sınırları içinde doğup denize dökülen en uzun akarsudur.`],
  [`Türkiye'nin en büyük adası aşağıdakilerden hangisidir?`, [`Bozcaada`, `Gökçeada`, `Marmara Adası`, `Avşa Adası`], 1, `Türkiye'nin yer şekilleri`, `Yüz ölçümü en büyük ada Gökçeada'dır.`],
  [`Aşağıdakilerden hangisi karstik arazilerde görülen yer şekillerinden biridir?`, [`Delta`, `Polye`, `Tombolo`, `Falez`], 1, `Yer şekilleri`, `Polye, kireçtaşı arazilerde oluşan karstik bir çukurluktur.`],
  [`Aşağıdaki ovalardan hangisi delta ovasıdır?`, [`Konya Ovası`, `Bafra Ovası`, `Erzurum Ovası`, `Muş Ovası`], 1, `Yer şekilleri`, `Bafra Ovası, Kızılırmak'ın taşıdığı alüvyonlarla oluşmuş delta ovasıdır.`],
  [`Aşağıdaki akarsulardan hangisi Karadeniz'e dökülmez?`, [`Kızılırmak`, `Yeşilırmak`, `Sakarya`, `Gediz`], 3, `Türkiye'nin akarsuları`, `Gediz Nehri Ege Denizi'ne dökülür.`],
  [`Makiler hangi iklim tipinin doğal bitki örtüsüdür?`, [`Karadeniz iklimi`, `Karasal iklim`, `Akdeniz iklimi`, `Muson iklimi`], 2, `Türkiye'nin iklimi`, `Makiler, Akdeniz ikliminin tipik bitki örtüsüdür.`],
  [`İç Anadolu Bölgesi'nin doğal bitki örtüsü aşağıdakilerden hangisidir?`, [`Bozkır`, `Orman`, `Maki`, `Savan`], 0, `Türkiye'nin iklimi`, `Karasal iklim koşullarında doğal bitki örtüsü bozkırdır.`],
  [`Bölge yüzölçümüne göre orman oranının en yüksek olduğu coğrafi bölgemiz aşağıdakilerden hangisidir?`, [`İç Anadolu`, `Karadeniz`, `Güneydoğu Anadolu`, `Doğu Anadolu`], 1, `Türkiye'nin bitki örtüsü`, `Bol yağış nedeniyle orman oranı en yüksek bölge Karadeniz Bölgesi'dir.`],
  [`Türkiye'de çay üretiminin en fazla yapıldığı il aşağıdakilerden hangisidir?`, [`Trabzon`, `Rize`, `Giresun`, `Ordu`], 1, `Tarım`, `Çay yetiştiriciliğinin merkezi Rize'dir.`],
  [`İncir yetiştiriciliğiyle ünlü ilimiz aşağıdakilerden hangisidir?`, [`Rize`, `Aydın`, `Gaziantep`, `Erzurum`], 1, `Tarım`, `Aydın, incir üretimiyle ünlüdür.`],
  [`Antep fıstığı en çok hangi coğrafi bölgemizde yetiştirilir?`, [`Karadeniz`, `Marmara`, `Güneydoğu Anadolu`, `Ege`], 2, `Tarım`, `Antep fıstığı Güneydoğu Anadolu'da yetişir.`],
  [`Türkiye'de taşkömürü çıkarılan il aşağıdakilerden hangisidir?`, [`Zonguldak`, `Manisa`, `Muğla`, `Kütahya`], 0, `Madenler`, `Taşkömürü Zonguldak havzasından çıkarılır.`],
  [`Türkiye'de petrolün çıkarıldığı illerimizden biri aşağıdakilerden hangisidir?`, [`Batman`, `Samsun`, `Edirne`, `Denizli`], 0, `Madenler`, `Batman ve çevresi petrol yataklarıyla bilinir.`],
  [`Nüfus yoğunluğu en düşük olan coğrafi bölgemiz aşağıdakilerden hangisidir?`, [`Marmara`, `Ege`, `Doğu Anadolu`, `Akdeniz`], 2, `Nüfus`, `Sert iklim ve engebeli arazi nedeniyle nüfus yoğunluğu en az Doğu Anadolu'dadır.`],
  [`Cumhuriyet döneminde yapılan ilk genel nüfus sayımı hangi yıl gerçekleşmiştir?`, [`1923`, `1927`, `1935`, `1945`], 1, `Nüfus`, `İlk genel nüfus sayımı 1927'de yapılmıştır.`, 1],
  [`Başlangıç meridyeni (0° boylamı) hangi şehirden geçer?`, [`Paris`, `Greenwich (Londra)`, `Roma`, `Berlin`], 1, `Dünya'nın şekli ve hareketleri`, `Başlangıç meridyeni Londra yakınlarındaki Greenwich'ten geçer.`],
  [`Türkiye hangi enlemler arasında yer alır?`, [`30° - 36° kuzey`, `36° - 42° kuzey`, `40° - 46° kuzey`, `20° - 26° kuzey`], 1, `Türkiye'nin coğrafi konumu`, `Türkiye 36° - 42° kuzey enlemleri arasındadır.`],
  [`Ortalama yükseltisi en fazla olan coğrafi bölgemiz aşağıdakilerden hangisidir?`, [`Marmara`, `Ege`, `Doğu Anadolu`, `Karadeniz`], 2, `Türkiye'nin yer şekilleri`, `Doğu Anadolu ortalama yükseltisi en fazla olan bölgedir.`],
  [`Muz yetiştiriciliğine en uygun yerlerimizden biri aşağıdakilerden hangisidir?`, [`Rize`, `Anamur`, `Bursa`, `Elazığ`], 1, `Tarım`, `Anamur (Mersin), ılıman iklimi nedeniyle muz tarımının merkezidir.`],
  [`Dünyanın en büyük okyanusu aşağıdakilerden hangisidir?`, [`Atlas Okyanusu`, `Hint Okyanusu`, `Büyük Okyanus`, `Arktik Okyanusu`], 2, `Dünya coğrafyası`, `Pasifik olarak da bilinen Büyük Okyanus en büyük okyanustur.`],
  [`Dünyanın en büyük sıcak çölü aşağıdakilerden hangisidir?`, [`Gobi`, `Büyük Sahra`, `Kalahari`, `Arabistan Çölü`], 1, `Dünya coğrafyası`, `En büyük sıcak çöl Büyük Sahra'dır.`],
  [`Everest Dağı hangi dağ sisteminde yer alır?`, [`Alpler`, `Andlar`, `Himalayalar`, `Kayalık Dağlar`], 2, `Dünya coğrafyası`, `Everest Dağı, Himalayalar'da bulunur.`],
  [`Türkiye'nin en kuzey noktası hangi ilimizde yer alır?`, [`Sinop`, `Samsun`, `Kırklareli`, `Zonguldak`], 0, `Türkiye'nin coğrafi konumu`, `En kuzey nokta Sinop'taki İnceburun'dur.`],
  [`Türkiye'nin en doğu ucu aşağıdaki illerimizden hangisindedir?`, [`Ağrı`, `Iğdır`, `Hakkâri`, `Van`], 1, `Türkiye'nin coğrafi konumu`, `Türkiye'nin en doğu noktası Iğdır sınırları içindedir.`],
  [`Gediz ve Büyük Menderes ovaları nasıl oluşmuştur?`, [`Delta`, `Graben (çöküntü)`, `Horst`, `Polye`], 1, `Yer şekilleri`, `Bu ovalar, fay hatlarıyla çöken graben alanlarında oluşmuştur.`],
  [`Ege kıyılarının girintili çıkıntılı olmasına neden olan kıyı tipi aşağıdakilerden hangisidir?`, [`Ria`, `Fiyort`, `Lagün`, `Falez`], 0, `Yer şekilleri`, `Dağların denize dik uzandığı Ege kıyıları ria tipi kıyıdır.`],
  [`Peribacaları (Kapadokya) hangi ilimiz sınırlarında yer alır?`, [`Kayseri`, `Nevşehir`, `Niğde`, `Aksaray`], 1, `Turizm`, `Peribacaları ve Göreme Nevşehir sınırlarındadır.`],
  [`Türkiye'de tuz üretiminin büyük bölümü hangi gölden karşılanır?`, [`Van Gölü`, `Tuz Gölü`, `Eğirdir Gölü`, `Burdur Gölü`], 1, `Madenler`, `Tuz üretiminin önemli bölümü Tuz Gölü'nden karşılanır.`],
];

const VATANDASLIK = [
  [`1982 Anayasası'na göre Türkiye Devleti'nin yönetim şekli aşağıdakilerden hangisidir?`, [`Monarşi`, `Cumhuriyet`, `Meşruti krallık`, `Federasyon`], 1, `Anayasa hukuku`, `Anayasa'nın 1. maddesine göre Türkiye Devleti bir Cumhuriyettir.`],
  [`Anayasa'nın değiştirilemeyeceği hükmü getirilen maddeleri aşağıdakilerden hangisidir?`, [`1., 2. ve 3. maddeler`, `1., 3. ve 5. maddeler`, `2., 3. ve 4. maddeler`, `1., 2. ve 4. maddeler`], 0, `Anayasa hukuku`, `4. maddeye göre 1., 2. ve 3. maddeler değiştirilemez ve değiştirilmesi teklif edilemez.`],
  [`Türkiye Büyük Millet Meclisi kaç milletvekilinden oluşur?`, [`450`, `550`, `600`, `650`], 2, `Yasama`, `TBMM 600 milletvekilinden oluşur.`, 1],
  [`Türkiye'de milletvekili seçilebilme yaşı kaçtır?`, [`18`, `21`, `25`, `30`], 0, `Yasama`, `Anayasa'ya göre milletvekili seçilme yaşı 18'dir.`, 1],
  [`Cumhurbaşkanı kaç yıllık süre için seçilir?`, [`4`, `5`, `6`, `7`], 1, `Yürütme`, `Cumhurbaşkanı beş yıl için seçilir.`, 1],
  [`Anayasa Mahkemesi kaç üyeden oluşur?`, [`11`, `15`, `17`, `21`], 1, `Yargı`, `Anayasa Mahkemesi 15 üyeden oluşur.`, 1],
  [`Temel hak ve özgürlüklerinin ihlal edildiğini düşünen kişilerin Anayasa Mahkemesi'ne yaptığı başvuru yoluna ne ad verilir?`, [`İptal davası`, `İtiraz yolu`, `Bireysel başvuru`, `Yetki uyuşmazlığı`], 2, `Yargı`, `Anayasa Mahkemesi'ne yapılan bu başvuru "bireysel başvuru"dur.`],
  [`İdari yargının en üst mahkemesi aşağıdakilerden hangisidir?`, [`Yargıtay`, `Danıştay`, `Sayıştay`, `Anayasa Mahkemesi`], 1, `Yargı`, `İdari yargının en yüksek mahkemesi Danıştay'dır.`],
  [`Kamu kurumlarının gelir ve giderlerini TBMM adına denetleyen kurum aşağıdakilerden hangisidir?`, [`Danıştay`, `Sayıştay`, `Yargıtay`, `Merkez Bankası`], 1, `Kamu yönetimi`, `Kamu mali denetimini TBMM adına Sayıştay yapar.`],
  [`Genel ve özel af ilanına hangi organ karar verir?`, [`Cumhurbaşkanı`, `TBMM`, `Anayasa Mahkemesi`, `Yüksek Seçim Kurulu`], 1, `Yasama`, `Genel ve özel af ilanına TBMM karar verir.`],
  [`Aşağıdakilerden hangisi özel hukuk dalıdır?`, [`Anayasa hukuku`, `İdare hukuku`, `Medeni hukuk`, `Ceza hukuku`], 2, `Hukuka giriş`, `Kişiler arası ilişkileri düzenleyen medeni hukuk özel hukuk dalıdır.`],
  [`Gerçek kişilik ne zaman başlar?`, [`Sağ ve tam doğumla`, `Ana rahmine düşmeyle`, `Erginlikle`, `Vatandaşlıkla`], 0, `Medeni hukuk`, `Medeni Kanun'a göre kişilik, çocuğun sağ olarak tam doğumuyla başlar.`],
  [`Medeni Kanun'a göre erginlik yaşı kaçtır?`, [`15`, `16`, `18`, `21`], 2, `Medeni hukuk`, `18 yaşını dolduran kişi ergin olur.`, 1],
  [`"Kanunsuz suç ve ceza olmaz" ilkesi aşağıdakilerden hangisidir?`, [`Kanunilik ilkesi`, `Orantılılık ilkesi`, `Şahsilik ilkesi`, `Yargı bağımsızlığı ilkesi`], 0, `Ceza hukuku`, `Suç ve cezanın ancak kanunla düzenlenebilmesi kanunilik ilkesidir.`],
  [`Birleşmiş Milletler İnsan Hakları Evrensel Beyannamesi hangi yıl kabul edilmiştir?`, [`1945`, `1948`, `1950`, `1966`], 1, `İnsan hakları`, `Beyanname 10 Aralık 1948'de kabul edilmiştir.`, 1],
  [`Avrupa İnsan Hakları Mahkemesi hangi şehirde bulunmaktadır?`, [`Cenevre`, `Strazburg`, `Lahey`, `Brüksel`], 1, `Uluslararası kuruluşlar`, `AİHM'nin merkezi Strazburg'dur.`],
  [`Türkiye'de oy kullanma yaşı kaçtır?`, [`16`, `18`, `21`, `25`], 1, `Siyasal haklar`, `Seçme yaşı 18'dir.`, 1],
];

const GUNCEL = [
  [`Birleşmiş Milletler'in genel merkezi hangi şehirdedir?`, [`Cenevre`, `New York`, `Paris`, `Brüksel`], 1, `Uluslararası kuruluşlar`, `BM'nin genel merkezi New York'tadır.`],
  [`Dünya Sağlık Örgütü'nün merkezi hangi şehirdedir?`, [`Cenevre`, `New York`, `Roma`, `Viyana`], 0, `Uluslararası kuruluşlar`, `DSÖ'nün merkezi Cenevre'dedir.`],
  [`Türkiye'nin kurucu üyeleri arasında bulunmadığı ancak 1952'de katıldığı askerî ittifak aşağıdakilerden hangisidir?`, [`NATO`, `Varşova Paktı`, `CENTO`, `SEATO`], 0, `Uluslararası kuruluşlar`, `Türkiye 1952'de NATO'ya üye olmuştur.`],
  [`Şanlıurfa'daki Göbeklitepe hangi yıl UNESCO Dünya Mirası Listesi'ne alınmıştır?`, [`2011`, `2015`, `2018`, `2021`], 2, `Kültür`, `Göbeklitepe, 2018'de UNESCO Dünya Mirası Listesi'ne girmiştir.`, 1],
  [`Türkiye Cumhuriyeti 29 Ekim 2023'te kuruluşunun kaçıncı yılını kutlamıştır?`, [`90`, `95`, `100`, `105`], 2, `Cumhuriyet`, `1923'ten itibaren 100. yıl 2023'te kutlanmıştır.`, 1],
  [`Aşağıdaki uluslararası ekonomik platformlardan hangisinin Türkiye üyesidir?`, [`G7`, `OPEC`, `G20`, `ASEAN`], 2, `Uluslararası kuruluşlar`, `Türkiye, G20 üyesi ülkelerden biridir.`],
  [`Nobel Barış Ödülü hangi şehirde verilir?`, [`Stockholm`, `Oslo`, `Cenevre`, `Lahey`], 1, `Uluslararası kuruluşlar`, `Nobel Barış Ödülü Oslo'da verilir.`],
  [`Uluslararası Para Fonu'nun (IMF) merkezi hangi şehirdedir?`, [`Londra`, `Washington`, `Cenevre`, `Paris`], 1, `Uluslararası kuruluşlar`, `IMF'nin merkezi Washington D.C.'dedir.`],
  [`UNESCO'nun merkezi hangi şehirdedir?`, [`Paris`, `Brüksel`, `Roma`, `Madrid`], 0, `Uluslararası kuruluşlar`, `UNESCO'nun merkezi Paris'tir.`],
  [`Avrupa Birliği'nin ortak para birimi aşağıdakilerden hangisidir?`, [`Sterlin`, `Euro`, `Frank`, `Dolar`], 1, `Uluslararası kuruluşlar`, `Euro bölgesi ülkelerinin ortak para birimidir.`],
];

const SETS = [['Türkçe', TURKCE], ['Matematik', MATEMATIK], ['Tarih', TARIH], ['Coğrafya', COGRAFYA], ['Vatandaşlık', VATANDASLIK], ['Güncel Bilgiler', GUNCEL]];
const LETTERS = ['A', 'B', 'C', 'D'];

// ---- deterministik sozde-rastgele (her calistirmada ayni sonuc)
let seed = 20261027;
function rnd() { seed = (seed * 1664525 + 1013904223) % 4294967296; return seed / 4294967296; }
function uuid() {
  const b = crypto.randomBytes(16);
  b[6] = (b[6] & 0x0f) | 0x40; b[8] = (b[8] & 0x3f) | 0x80;
  const h = b.toString('hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-${h.slice(12, 16)}-${h.slice(16, 20)}-${h.slice(20)}`;
}

// ---- onceki calistirmadan id'leri koru (ayni metin -> ayni id)
const outPath = path.join(__dirname, 'sorular2.json');
const prevIds = {};
if (fs.existsSync(outPath)) {
  for (const q of JSON.parse(fs.readFileSync(outPath, 'utf8'))) prevIds[q.text] = q.id;
}

const out = [];
const errors = [];
for (const [category, items] of SETS) {
  for (const it of items) {
    const [text, opts, correctIdx, topic, explanation, ordered] = it;
    if (opts.length !== 4) errors.push(`4 sik degil: ${text.slice(0, 40)}`);
    if (new Set(opts).size !== 4) errors.push(`tekrarlayan sik: ${text.slice(0, 40)}`);
    if (correctIdx < 0 || correctIdx > 3) errors.push(`gecersiz dogru index: ${text.slice(0, 40)}`);
    out.push({ category, text, opts: opts.slice(), correct: opts[correctIdx], topic, explanation, ordered: !!ordered });
  }
}
if (errors.length) { console.error(errors.join('\n')); process.exit(1); }

// ---- siklari dagit: siralilar artan/azalan, digerleri karisik; dogru sik dagilimini dengele
const targetPer = Math.ceil(out.length / 4);
const counts = { A: 0, B: 0, C: 0, D: 0 };
for (const q of out) {
  let arrangements = [];
  if (q.ordered) {
    arrangements = [q.opts.slice(), q.opts.slice().reverse()];
  } else {
    for (let t = 0; t < 12; t++) {
      const a = q.opts.slice();
      for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; }
      arrangements.push(a);
    }
  }
  // dogru siki en az dolu harfe koyan duzeni sec
  arrangements.sort((x, y) => counts[LETTERS[x.indexOf(q.correct)]] - counts[LETTERS[y.indexOf(q.correct)]]);
  const best = arrangements[0];
  q.opts = best;
  q.letter = LETTERS[best.indexOf(q.correct)];
  counts[q.letter]++;
}

const final = out.map((q) => ({
  id: prevIds[q.text] || uuid(),
  text: q.text,
  optionA: q.opts[0], optionB: q.opts[1], optionC: q.opts[2], optionD: q.opts[3],
  correct: q.letter,
  category: q.category,
  topic: q.topic,
  explanation: q.explanation,
}));

fs.writeFileSync(outPath, JSON.stringify(final, null, 2) + '\n', 'utf8');
const byCat = {};
final.forEach((q) => { byCat[q.category] = (byCat[q.category] || 0) + 1; });
console.log('Toplam:', final.length, byCat);
console.log('Dogru sik dagilimi:', counts);
