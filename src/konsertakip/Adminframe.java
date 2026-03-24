package konsertakip;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Adminframe extends Frame implements ActionListener {

    static final Color KOYU    = new Color(90, 20, 60);
    static final Color ORTA    = new Color(140, 40, 90);
    static final Color ACIK    = new Color(180, 50, 110);
    static final Color ALTIN   = new Color(255, 200, 50);
    static final Color BEYAZ   = new Color(255, 255, 255);
    static final Color GRI     = new Color(220, 215, 230);
    static final Color SIYAH   = new Color(0, 0, 0);

    static final Font F_BASLIK = new Font("Dialog", Font.BOLD, 16);
    static final Font F_LABEL  = new Font("Dialog", Font.BOLD, 13);
    static final Font F_FIELD  = new Font("Dialog", Font.PLAIN, 13);
    static final Font F_BUTON  = new Font("Dialog", Font.BOLD, 12);
    static final Font F_LIST   = new Font("Monospaced", Font.PLAIN, 12);

    static final String[] TURLER = {"Pop", "Rap", "Rock", "Arabesk"};

    Connection connection;

    // Sekme butonlari
    Button btnTabKonser, btnTabBiletler, btnTabKullanicilar, btnTabRapor, btnCikis;

    // --- KONSER SEKMESI ---
    Panel pnlKonser;
    TextField tfAd, tfTarih, tfKonum, tfFiyat, tfKapasite, tfGuncId, tfGuncAd, tfGuncTarih, tfGuncKonum, tfGuncFiyat, tfGuncKapasite;
    Choice chTur, chGuncTur;
    Button btnEkle, btnSil, btnGuncelle, btnGuncYukle;
    List   konserList;

    // --- BILET SEKMESI ---
    Panel pnlBiletler;
    List  tumBiletList;
    Button btnBiletIptal;
    Label  lblTumCiro;

    // --- KULLANICI SEKMESI ---
    Panel pnlKullanicilar;
    List  kullaniciList;
    Button btnKullaniciSil;

    // --- RAPOR SEKMESI ---
    Panel pnlRapor;
    Label lblRKonser, lblRBilet, lblRKullanici, lblRCiro, lblRKapasite, lblREnCok;
    Button btnRaporYenile;

    Panel pnlUst, pnlIcerik;

    public Adminframe(Connection con) {
        super("Konser Takip - ADMIN");
        this.connection = con;

        setSize(680, 720);
        setResizable(false);
        setBackground(KOYU);
        setLayout(new BorderLayout());

        // UST BAR
        pnlUst = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlUst.setBackground(ACIK);
        pnlUst.setPreferredSize(new Dimension(680, 50));

        Label lblTitle = new Label("ADMIN PANELI", Label.LEFT);
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 15));
        lblTitle.setForeground(ALTIN);

        btnTabKonser      = tabBtn("KONSERLER");
        btnTabBiletler    = tabBtn("TUM BILETLER");
        btnTabKullanicilar= tabBtn("KULLANICILAR");
        btnTabRapor       = tabBtn("RAPOR");
        btnCikis          = tabBtn("CIKIS");

        pnlUst.add(lblTitle);
        pnlUst.add(btnTabKonser);
        pnlUst.add(btnTabBiletler);
        pnlUst.add(btnTabKullanicilar);
        pnlUst.add(btnTabRapor);
        pnlUst.add(btnCikis);
        add(pnlUst, BorderLayout.NORTH);

        // ICERIK (CardLayout)
        pnlIcerik = new Panel(new CardLayout());
        pnlIcerik.setBackground(KOYU);

        olusturKonserSekmesi();
        olusturBiletlerSekmesi();
        olusturKullanicilarSekmesi();
        olusturRaporSekmesi();

        pnlIcerik.add(pnlKonser,       "konser");
        pnlIcerik.add(pnlBiletler,     "biletler");
        pnlIcerik.add(pnlKullanicilar, "kullanicilar");
        pnlIcerik.add(pnlRapor,        "rapor");
        add(pnlIcerik, BorderLayout.CENTER);

        konserleriYukle();

        Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ekran.width - 680) / 2, (ekran.height - 720) / 2);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new LoginFrame(); }
        });
    }

    private Button tabBtn(String etiket) {
        Button b = new Button(etiket);
        b.setFont(F_BUTON); b.setForeground(SIYAH);
        b.setPreferredSize(new Dimension(110, 30));
        b.addActionListener(this);
        return b;
    }

    // =====================================================================
    //  KONSER SEKMESI
    // =====================================================================
    private void olusturKonserSekmesi() {
        pnlKonser = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        pnlKonser.setBackground(KOYU);

        // --- EKLEME FORMU ---
        Panel pEkleBas = baslikPaneli("Yeni Konser Ekle", 650);
        pnlKonser.add(pEkleBas);

        Panel pEkleForm = new Panel(new GridLayout(3, 4, 8, 8));
        pEkleForm.setBackground(ORTA);
        pEkleForm.setPreferredSize(new Dimension(650, 110));

        Label lAd = new Label("  Ad:"); stilLabel(lAd); tfAd = new TextField(); stilField(tfAd);
        Label lTar = new Label("  Tarih:"); stilLabel(lTar); tfTarih = new TextField(); stilField(tfTarih);
        Label lKon = new Label("  Konum:"); stilLabel(lKon); tfKonum = new TextField(); stilField(tfKonum);
        Label lFiy = new Label("  Fiyat:"); stilLabel(lFiy); tfFiyat = new TextField(); stilField(tfFiyat);
        Label lTur = new Label("  Tur:"); stilLabel(lTur);
        chTur = new Choice(); chTur.setFont(F_FIELD);
        for (String t : TURLER) chTur.add(t);
        Label lKap = new Label("  Kapasite:"); stilLabel(lKap); tfKapasite = new TextField("100"); stilField(tfKapasite);

        pEkleForm.add(lAd); pEkleForm.add(tfAd);
        pEkleForm.add(lTar); pEkleForm.add(tfTarih);
        pEkleForm.add(lKon); pEkleForm.add(tfKonum);
        pEkleForm.add(lFiy); pEkleForm.add(tfFiyat);
        pEkleForm.add(lTur); pEkleForm.add(chTur);
        pEkleForm.add(lKap); pEkleForm.add(tfKapasite);
        pnlKonser.add(pEkleForm);

        Panel pEkleBtn = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pEkleBtn.setBackground(KOYU);
        pEkleBtn.setPreferredSize(new Dimension(650, 42));
        btnEkle = aksiyon("+ EKLE"); btnSil = aksiyon("- SIL");
        pEkleBtn.add(btnEkle); pEkleBtn.add(btnSil);
        pnlKonser.add(pEkleBtn);

        // --- LISTE ---
        Panel pListeBas = baslikPaneli("Konser Listesi", 650);
        pnlKonser.add(pListeBas);

        konserList = new List(7, false);
        konserList.setBackground(new Color(70, 10, 50));
        konserList.setForeground(GRI); konserList.setFont(F_LIST);
        konserList.setPreferredSize(new Dimension(650, 130));
        pnlKonser.add(konserList);

        // --- GUNCELLEME ---
        Panel pGuncBas = baslikPaneli("Secili Konseri Guncelle", 650);
        pnlKonser.add(pGuncBas);

        Panel pGuncForm = new Panel(new GridLayout(3, 4, 8, 8));
        pGuncForm.setBackground(ORTA);
        pGuncForm.setPreferredSize(new Dimension(650, 110));

        Label lgId = new Label("  ID:"); stilLabel(lgId); tfGuncId = new TextField(); tfGuncId.setEditable(false); stilField(tfGuncId);
        Label lgAd = new Label("  Ad:"); stilLabel(lgAd); tfGuncAd = new TextField(); stilField(tfGuncAd);
        Label lgTar = new Label("  Tarih:"); stilLabel(lgTar); tfGuncTarih = new TextField(); stilField(tfGuncTarih);
        Label lgKon = new Label("  Konum:"); stilLabel(lgKon); tfGuncKonum = new TextField(); stilField(tfGuncKonum);
        Label lgFiy = new Label("  Fiyat:"); stilLabel(lgFiy); tfGuncFiyat = new TextField(); stilField(tfGuncFiyat);
        Label lgTur = new Label("  Tur:"); stilLabel(lgTur);
        chGuncTur = new Choice(); chGuncTur.setFont(F_FIELD);
        for (String t : TURLER) chGuncTur.add(t);
        Label lgKap = new Label("  Kapasite:"); stilLabel(lgKap); tfGuncKapasite = new TextField(); stilField(tfGuncKapasite);

        pGuncForm.add(lgId); pGuncForm.add(tfGuncId);
        pGuncForm.add(lgAd); pGuncForm.add(tfGuncAd);
        pGuncForm.add(lgTar); pGuncForm.add(tfGuncTarih);
        pGuncForm.add(lgKon); pGuncForm.add(tfGuncKonum);
        pGuncForm.add(lgFiy); pGuncForm.add(tfGuncFiyat);
        pGuncForm.add(lgTur); pGuncForm.add(chGuncTur);
        pnlKonser.add(pGuncForm);

        Panel pGuncBtn = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pGuncBtn.setBackground(KOYU);
        pGuncBtn.setPreferredSize(new Dimension(650, 42));
        btnGuncYukle = aksiyon("SECILENI YUKLE");
        btnGuncelle  = aksiyon("GUNCELLE");
        pGuncBtn.add(btnGuncYukle); pGuncBtn.add(btnGuncelle);
        pnlKonser.add(pGuncBtn);
    }

    // =====================================================================
    //  BILET SEKMESI
    // =====================================================================
    private void olusturBiletlerSekmesi() {
        pnlBiletler = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlBiletler.setBackground(KOYU);
        pnlBiletler.add(baslikPaneli("Tum Kullanici Biletleri", 650));

        tumBiletList = new List(20, false);
        tumBiletList.setBackground(new Color(70, 10, 50));
        tumBiletList.setForeground(GRI); tumBiletList.setFont(F_LIST);
        tumBiletList.setPreferredSize(new Dimension(650, 480));
        pnlBiletler.add(tumBiletList);

        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pAlt.setBackground(ORTA);
        pAlt.setPreferredSize(new Dimension(650, 52));
        lblTumCiro = new Label("Toplam Ciro: 0.00 TL", Label.LEFT);
        lblTumCiro.setFont(new Font("Dialog", Font.BOLD, 14)); lblTumCiro.setForeground(ALTIN);
        btnBiletIptal = aksiyon("BILETI IPTAL ET");
        pAlt.add(lblTumCiro); pAlt.add(btnBiletIptal);
        pnlBiletler.add(pAlt);
    }

    // =====================================================================
    //  KULLANICI SEKMESI
    // =====================================================================
    private void olusturKullanicilarSekmesi() {
        pnlKullanicilar = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlKullanicilar.setBackground(KOYU);
        pnlKullanicilar.add(baslikPaneli("Kayitli Kullanicilar", 650));

        kullaniciList = new List(22, false);
        kullaniciList.setBackground(new Color(70, 10, 50));
        kullaniciList.setForeground(GRI); kullaniciList.setFont(F_LIST);
        kullaniciList.setPreferredSize(new Dimension(650, 540));
        pnlKullanicilar.add(kullaniciList);

        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pAlt.setBackground(ORTA);
        pAlt.setPreferredSize(new Dimension(650, 50));
        btnKullaniciSil = aksiyon("KULLANICI SIL");
        pAlt.add(btnKullaniciSil);
        pnlKullanicilar.add(pAlt);
    }

    // =====================================================================
    //  RAPOR SEKMESI
    // =====================================================================
    private void olusturRaporSekmesi() {
        pnlRapor = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        pnlRapor.setBackground(KOYU);
        pnlRapor.add(baslikPaneli("Sistem Raporu ve Istatistikler", 650));

        Panel pKartlar = new Panel(new GridLayout(3, 2, 15, 15));
        pKartlar.setBackground(KOYU);
        pKartlar.setPreferredSize(new Dimension(640, 390));

        lblRKonser   = raporKart("Toplam Konser",        "-");
        lblRBilet    = raporKart("Toplam Satilan Bilet", "-");
        lblRKullanici= raporKart("Kayitli Kullanici",    "-");
        lblRCiro     = raporKart("Toplam Ciro",          "-");
        lblRKapasite = raporKart("Doluluk Orani",        "-");
        lblREnCok    = raporKart("En Cok Satan Konser",  "-");

        pKartlar.add(kartSarla(lblRKonser));
        pKartlar.add(kartSarla(lblRBilet));
        pKartlar.add(kartSarla(lblRKullanici));
        pKartlar.add(kartSarla(lblRCiro));
        pKartlar.add(kartSarla(lblRKapasite));
        pKartlar.add(kartSarla(lblREnCok));
        pnlRapor.add(pKartlar);

        btnRaporYenile = aksiyon("RAPORU YENILE");
        pnlRapor.add(btnRaporYenile);
    }

    private Label raporKart(String baslik, String deger) {
        Label l = new Label(baslik + ": " + deger, Label.CENTER);
        l.setFont(new Font("Dialog", Font.BOLD, 14));
        l.setForeground(ALTIN);
        return l;
    }

    private Panel kartSarla(Label l) {
        Panel p = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 35));
        p.setBackground(ORTA);
        p.add(l);
        return p;
    }

    private Panel baslikPaneli(String metin, int genislik) {
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        p.setBackground(ACIK);
        p.setPreferredSize(new Dimension(genislik, 36));
        Label l = new Label(metin, Label.LEFT);
        l.setFont(F_BASLIK); l.setForeground(ALTIN);
        p.add(l);
        return p;
    }

    private Button aksiyon(String etiket) {
        Button b = new Button(etiket);
        b.setFont(F_BUTON); b.setForeground(SIYAH);
        b.setPreferredSize(new Dimension(160, 32));
        b.addActionListener(this);
        return b;
    }

    // =====================================================================
    //  ACTION LISTENER
    // =====================================================================
    public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) pnlIcerik.getLayout();
        Object k = e.getSource();

        if      (k == btnTabKonser)       { cl.show(pnlIcerik, "konser");       konserleriYukle(); }
        else if (k == btnTabBiletler)     { cl.show(pnlIcerik, "biletler");     tumBiletleriYukle(); }
        else if (k == btnTabKullanicilar) { cl.show(pnlIcerik, "kullanicilar"); kullanicilariYukle(); }
        else if (k == btnTabRapor)        { cl.show(pnlIcerik, "rapor");        raporuYukle(); }
        else if (k == btnCikis)           { dispose(); new LoginFrame(); }
        else if (k == btnEkle)            konserEkle();
        else if (k == btnSil)             konserSil();
        else if (k == btnGuncYukle)       guncellemeAlanlariDoldur();
        else if (k == btnGuncelle)        konserGuncelle();
        else if (k == btnBiletIptal)      biletIptalEt();
        else if (k == btnKullaniciSil)    kullaniciSil();
        else if (k == btnRaporYenile)     raporuYukle();
    }

    // =====================================================================
    //  VERİTABANI METOTlari
    // =====================================================================
    private void konserleriYukle() {
        konserList.removeAll();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT id,ad,tarih,konum,fiyat,tur,kapasite,satilan FROM Konser ORDER BY id");
            while (rs.next()) {
                int kap = rs.getInt("kapasite"), sat = rs.getInt("satilan");
                konserList.add(String.format("#%d | %-20s | %s | %-12s | %.0f TL | %s | Kapasite:%d/%d",
                        rs.getInt("id"), rs.getString("ad"), rs.getString("tarih"),
                        rs.getString("konum"), rs.getDouble("fiyat"),
                        rs.getString("tur"), sat, kap));
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void konserEkle() {
        String ad = tfAd.getText().trim(), tarih = tfTarih.getText().trim(),
                konum = tfKonum.getText().trim(), fiyatStr = tfFiyat.getText().trim(),
                kapStr = tfKapasite.getText().trim();
        String tur = chTur.getSelectedItem();
        if (ad.isEmpty() || tarih.isEmpty() || konum.isEmpty() || fiyatStr.isEmpty()) return;
        try {
            int kap = kapStr.isEmpty() ? 100 : Integer.parseInt(kapStr);
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Konser (ad,tarih,konum,fiyat,tur,kapasite,satilan) VALUES (?,?,?,?,?,?,0)");
            ps.setString(1,ad); ps.setString(2,tarih); ps.setString(3,konum);
            ps.setDouble(4,Double.parseDouble(fiyatStr)); ps.setString(5,tur); ps.setInt(6,kap);
            ps.executeUpdate();
            tfAd.setText(""); tfTarih.setText(""); tfKonum.setText(""); tfFiyat.setText(""); tfKapasite.setText("100");
            konserleriYukle();
        } catch (Exception ex) { System.out.println("Ekleme hatasi: " + ex.getMessage()); }
    }

    private void konserSil() {
        String s = konserList.getSelectedItem(); if (s == null) return;
        try {
            int id = Integer.parseInt(s.split("\\|")[0].replace("#","").trim());
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Konser WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate(); konserleriYukle();
        } catch (Exception ex) { System.out.println("Silme hatasi: " + ex.getMessage()); }
    }

    private void guncellemeAlanlariDoldur() {
        String s = konserList.getSelectedItem(); if (s == null) return;
        try {
            int id = Integer.parseInt(s.split("\\|")[0].replace("#","").trim());
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM Konser WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfGuncId.setText(String.valueOf(rs.getInt("id")));
                tfGuncAd.setText(rs.getString("ad"));
                tfGuncTarih.setText(rs.getString("tarih"));
                tfGuncKonum.setText(rs.getString("konum"));
                tfGuncFiyat.setText(String.valueOf(rs.getDouble("fiyat")));
                tfGuncKapasite.setText(String.valueOf(rs.getInt("kapasite")));
                String tur = rs.getString("tur");
                for (int i = 0; i < chGuncTur.getItemCount(); i++)
                    if (chGuncTur.getItem(i).equals(tur)) { chGuncTur.select(i); break; }
            }
        } catch (Exception ex) { System.out.println("Yukleme hatasi: " + ex.getMessage()); }
    }

    private void konserGuncelle() {
        String idStr = tfGuncId.getText().trim(); if (idStr.isEmpty()) return;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE Konser SET ad=?,tarih=?,konum=?,fiyat=?,tur=?,kapasite=? WHERE id=?");
            ps.setString(1, tfGuncAd.getText().trim());
            ps.setString(2, tfGuncTarih.getText().trim());
            ps.setString(3, tfGuncKonum.getText().trim());
            ps.setDouble(4, Double.parseDouble(tfGuncFiyat.getText().trim()));
            ps.setString(5, chGuncTur.getSelectedItem());
            ps.setInt(6, Integer.parseInt(tfGuncKapasite.getText().trim()));
            ps.setInt(7, Integer.parseInt(idStr));
            ps.executeUpdate();
            konserleriYukle();
        } catch (Exception ex) { System.out.println("Guncelleme hatasi: " + ex.getMessage()); }
    }

    private void tumBiletleriYukle() {
        tumBiletList.removeAll();
        double ciro = 0;
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT b.id, k.kullanici_adi, k.ad, k.soyad, b.konser_ad, b.konser_tur, " +
                            "b.adet, b.toplam, b.satin_alma_tarihi " +
                            "FROM Bilet b LEFT JOIN Kullanici k ON b.kullanici_id=k.id ORDER BY b.id DESC");
            while (rs.next()) {
                ciro += rs.getDouble("toplam");
                String kulAdi = rs.getString("kullanici_adi") != null ? rs.getString("kullanici_adi") : "?";
                String adSoy  = (rs.getString("ad") != null ? rs.getString("ad") : "") + " " +
                        (rs.getString("soyad") != null ? rs.getString("soyad") : "");
                tumBiletList.add(String.format("#%d | %s (%s) | %s | %s | %d adet | %.2f TL | %s",
                        rs.getInt("id"), kulAdi, adSoy.trim(),
                        rs.getString("konser_ad"), rs.getString("konser_tur"),
                        rs.getInt("adet"), rs.getDouble("toplam"),
                        rs.getString("satin_alma_tarihi")));
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        lblTumCiro.setText("Toplam Ciro: " + String.format("%.2f", ciro) + " TL");
    }

    private void biletIptalEt() {
        String s = tumBiletList.getSelectedItem(); if (s == null) return;
        try {
            int id = Integer.parseInt(s.split("\\|")[0].replace("#","").trim());
            // Bilet adet bilgisini al, satilan sayisini duzenle
            PreparedStatement getPs = connection.prepareStatement(
                    "SELECT konser_id, adet FROM Bilet WHERE id=?");
            getPs.setInt(1, id);
            ResultSet rs = getPs.executeQuery();
            if (rs.next()) {
                int konserID = rs.getInt("konser_id");
                int adet     = rs.getInt("adet");
                PreparedStatement upPs = connection.prepareStatement(
                        "UPDATE Konser SET satilan = satilan - ? WHERE id=?");
                upPs.setInt(1, adet); upPs.setInt(2, konserID); upPs.executeUpdate();
            }
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Bilet WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate();
            tumBiletleriYukle();
        } catch (Exception ex) { System.out.println("Bilet iptal hatasi: " + ex.getMessage()); }
    }

    private void kullanicilariYukle() {
        kullaniciList.removeAll();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT k.id, k.ad, k.soyad, k.kullanici_adi, COUNT(b.id) as bilet_sayisi, " +
                            "COALESCE(SUM(b.toplam),0) as toplam_harcama " +
                            "FROM Kullanici k LEFT JOIN Bilet b ON k.id=b.kullanici_id " +
                            "GROUP BY k.id ORDER BY k.id");
            while (rs.next())
                kullaniciList.add(String.format("#%d | %s %s | @%s | %d bilet | %.2f TL harcama",
                        rs.getInt("id"), rs.getString("ad"), rs.getString("soyad"),
                        rs.getString("kullanici_adi"), rs.getInt("bilet_sayisi"),
                        rs.getDouble("toplam_harcama")));
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void kullaniciSil() {
        String s = kullaniciList.getSelectedItem(); if (s == null) return;
        try {
            int id = Integer.parseInt(s.split("\\|")[0].replace("#","").trim());
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Kullanici WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate(); kullanicilariYukle();
        } catch (Exception ex) { System.out.println(ex.getMessage()); }
    }

    private void raporuYukle() {
        try {
            ResultSet r1 = connection.createStatement().executeQuery("SELECT COUNT(*) FROM Konser");
            if (r1.next()) lblRKonser.setText("Toplam Konser: " + r1.getInt(1));

            ResultSet r2 = connection.createStatement().executeQuery(
                    "SELECT COUNT(*), COALESCE(SUM(toplam),0) FROM Bilet");
            if (r2.next()) {
                lblRBilet.setText("Toplam Satilan Bilet: " + r2.getInt(1));
                lblRCiro.setText("Toplam Ciro: " + String.format("%.2f", r2.getDouble(2)) + " TL");
            }

            ResultSet r3 = connection.createStatement().executeQuery("SELECT COUNT(*) FROM Kullanici");
            if (r3.next()) lblRKullanici.setText("Kayitli Kullanici: " + r3.getInt(1));

            ResultSet r4 = connection.createStatement().executeQuery(
                    "SELECT COALESCE(SUM(satilan),0)*1.0/NULLIF(COALESCE(SUM(kapasite),1),0)*100 as oran FROM Konser");
            if (r4.next()) lblRKapasite.setText(String.format("Doluluk Orani: %.1f%%", r4.getDouble("oran")));

            ResultSet r5 = connection.createStatement().executeQuery(
                    "SELECT konser_ad, SUM(adet) as toplam FROM Bilet GROUP BY konser_ad ORDER BY toplam DESC LIMIT 1");
            if (r5.next()) lblREnCok.setText("En Cok Satan: " + r5.getString("konser_ad") + " (" + r5.getInt("toplam") + ")");
            else           lblREnCok.setText("En Cok Satan: -");

        } catch (SQLException ex) { System.out.println("Rapor hatasi: " + ex.getMessage()); }
    }

    private void stilLabel(Label l) { l.setFont(F_LABEL); l.setForeground(GRI); }
    private void stilField(TextField tf) {
        tf.setFont(F_FIELD);
        tf.setBackground(new Color(70, 10, 50));
        tf.setForeground(BEYAZ);
    }
}