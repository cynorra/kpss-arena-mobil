package com.cynorra.kpssarena.data.local;

import com.cynorra.kpssarena.domain.model.DailyTest;
import com.cynorra.kpssarena.domain.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Bundled offline 5-option (A-E) ÖSYM KPSS fallback question bank.
 * Ensures Daily Test never fails when Firestore is unreachable or guest auth is unavailable.
 */
public final class LocalKpssQuestionBank {

    private LocalKpssQuestionBank() {}

    public static DailyTest buildDailyTest(String date) {
        List<Question> all = getAllQuestions();
        long seed = date != null ? date.hashCode() : System.currentTimeMillis();
        Collections.shuffle(all, new Random(seed));
        List<Question> picked = new ArrayList<>(all.subList(0, Math.min(12, all.size())));
        DailyTest test = new DailyTest();
        test.setDate(date);
        test.setActive(true);
        test.setQuestions(picked);
        return test;
    }

    private static Question q(String id, String cat, String text,
                              String a, String b, String c, String d, String e,
                              String correct, String exp) {
        Question q = new Question();
        q.setId(id);
        q.setCategory(cat);
        q.setText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setOptionE(e);
        q.setCorrectAnswer(correct);
        q.setExplanation(exp);
        return q;
    }

    public static List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(q("loc_1", "Tarih",
                "Türk tarihinde 'İlk Türk-İslam yazılı edebi eseri' olarak kabul edilen Kutadgu Bilig'in yazarı kimdir?",
                "Kaşgarlı Mahmut", "Yusuf Has Hacip", "Edip Ahmet Yükneki", "Hoca Ahmet Yesevi", "Nizamülmülk",
                "B", "Kutadgu Bilig (Mutluluk Veren Bilgi), 1069 yılında Karahanlı döneminde Yusuf Has Hacip tarafından yazılmıştır."));
        list.add(q("loc_2", "Tarih",
                "Milli Mücadele döneminde 'Misak-ı Milli' kararları aşağıdakilerden hangisinde kabul edilmiştir?",
                "Erzurum Kongresi", "Sivas Kongresi", "Son Osmanlı Mebusan Meclisi", "I. TBMM", "Amasya Görüşmeleri",
                "C", "Misak-ı Milli kararları 28 Ocak 1920'de Son Osmanlı Mebusan Meclisi'nde kabul edilmiştir."));
        list.add(q("loc_3", "Tarih",
                "Osmanlı Devleti'nde ilk kez Batı tarzı ıslahatların yapıldığı ve matbaanın kurulduğu dönem hangisidir?",
                "Tanzimat Dönemi", "Lale Devri", "Islahat Dönemi", "I. Meşrutiyet", "Duraklama Dönemi",
                "B", "1718-1730 yılları arasındaki Lale Devri'nde ilk Türk matbaası kurulmuş ve Batı'ya geçici elçilikler gönderilmiştir."));
        list.add(q("loc_4", "Coğrafya",
                "Türkiye'de aşağıdaki kıyı tiplerinden hangisi görülmez?",
                "Boyuna kıyı", "Enine kıyı", "Ria tipi kıyı", "Dalmaçya tipi kıyı", "Fiyort tipi kıyı",
                "E", "Fiyort tipi kıyılar yüksek enlemlerde buzulların deniz seviyesine inmesiyle oluşur; Türkiye orta kuşakta olduğu için görülmez."));
        list.add(q("loc_5", "Coğrafya",
                "Türkiye'nin en uzun kara sınırına sahip olduğu komşu ülke aşağıdakilerden hangisidir?",
                "İran", "Irak", "Suriye", "Yunanistan", "Bulgaristan",
                "C", "Türkiye'nin en uzun kara sınırı yaklaşık 911 km ile Suriye iledir."));
        list.add(q("loc_6", "Coğrafya",
                "Aşağıdaki ovalardan hangisi tektonik kökenli bir ova değildir?",
                "Erzincan Ovası", "Konya Ovası", "Çukurova", "Niksar Ovası", "Iğdır Ovası",
                "C", "Çukurova, Seyhan ve Ceyhan nehirlerinin taşıdığı alüvyonlarla oluşmuş bir delta ovasıdır."));
        list.add(q("loc_7", "Vatandaşlık",
                "1982 Anayasası'na göre TBMM seçimleri normal şartlarda kaç yılda bir yapılır?",
                "3 yıl", "4 yıl", "5 yıl", "6 yıl", "7 yıl",
                "C", "2017 Anayasa değişikliği ile TBMM ve Cumhurbaşkanlığı seçimleri 5 yılda bir aynı gün yapılır."));
        list.add(q("loc_8", "Vatandaşlık",
                "Aşağıdakilerden hangisi 1982 Anayasası'nda yer alan yüksek mahkemelerden biri değildir?",
                "Anayasa Mahkemesi", "Yargıtay", "Danıştay", "Uyuşmazlık Mahkemesi", "Sayıştay",
                "E", "Sayıştay, TBMM adına mali denetim yapan anayasal bir kurumdur ancak Anayasa'da sayılan yüksek mahkemelerden biri değildir."));
        list.add(q("loc_9", "Vatandaşlık",
                "Bir kimsenin kendi fiilleriyle hak edinebilme ve borç altına girebilme ehliyetine ne ad verilir?",
                "Hak ehliyeti", "Fiil ehliyeti", "Dava ehliyeti", "Hukuki kişilik", "Tüzel kişilik",
                "B", "Ayırt etme gücüne sahip, ergin ve kısıtlı olmayan kişilerin sahip olduğu ehliyet 'fiil ehliyeti'dir."));
        list.add(q("loc_10", "Türkçe",
                "Aşağıdaki cümlelerin hangisinde 'de/da' bağlacının yazımıyla ilgili bir yanlışlık yapılmıştır?",
                "Bu konuyu sen de çok iyi biliyorsun.", "Sınavda başarılı olmak için planlı çalışmalısın.", "Kitabı masanın üstün de unutmuşum.", "Akşamki toplantıya o da katılacak.", "Yarın hava biraz da olsa ısınacak.",
                "C", "'üstünde' sözcüğündeki '-de' bulunma hal ekidir ve sözcüğe bitişik yazılmalıdır."));
        list.add(q("loc_11", "Türkçe",
                "Aşağıdaki sözcüklerden hangisi yapım eki alarak yeni bir anlam kazanmıştır?",
                "Evler", "Kitabım", "Gözlük", "Okula", "Masada",
                "C", "'Göz' köküne gelen '-lük' eki isimden isim yapım ekidir ve yeni bir kavram türetir."));
        list.add(q("loc_12", "Matematik",
                "Bir sınıftaki öğrencilerin %40'ı kızdır. Sınıfta 18 erkek öğrenci olduğuna göre sınıf mevcudu kaçtır?",
                "24", "28", "30", "32", "36",
                "C", "Erkekler sınıfın %60'ıdır. 0,60 × x = 18 ise x = 30 bulunur."));
        list.add(q("loc_13", "Matematik",
                "Ardışık üç tek doğal sayının toplamı 57 olduğuna göre bu sayıların en büyüğü kaçtır?",
                "17", "19", "21", "23", "25",
                "C", "Ortanca sayı 57 / 3 = 19'dur. Ardışık tek sayılar 17, 19, 21 olduğundan en büyüğü 21'dir."));
        list.add(q("loc_14", "Güncel Bilgiler",
                "UNESCO Dünya Mirası Listesi'nde yer alan ve 'tarihin sıfır noktası' olarak bilinen Göbeklitepe hangi ilimizdedir?",
                "Gaziantep", "Şanlıurfa", "Mardin", "Adıyaman", "Diyarbakır",
                "B", "Göbeklitepe, Şanlıurfa ili sınırları içerisinde yer alır ve 2018'de UNESCO Dünya Mirası Listesi'ne girmiştir."));
        return list;
    }
}
