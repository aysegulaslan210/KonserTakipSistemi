package konsertakip;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Kullaniciframe extends Frame implements ActionListener, KeyListener, ItemListener {

    static final Color KOYU   = new Color(90, 20, 60);
    static final Color ORTA   = new Color(140, 40, 90);
    static final Color ACIK   = new Color(180, 50, 110);
    static final Color ALTIN  = new Color(255, 200, 50);
    static final Color BEYAZ  = new Color(255, 255, 255);
    static final Color GRI    = new Color(220, 215, 230);
    static final Color SIYAH  = new Color(0, 0, 0);
    static final Color YESIL  = new Color(80, 200, 120);

    static final Font F_BASLIK = new Font("Dialog", Font.BOLD, 16);
    static final Font F_LABEL  = new Font("Dialog", Font.BOLD, 13);
    static final Font F_FIELD  = new Font("Dialog", Font.PLAIN, 13);
    static final Font F_BUTON  = new Font("Dialog", Font.BOLD, 12);
    static final Font F_LIST   = new Font("Monospaced", Font.PLAIN, 12);

    int    kullaniciID;
    String kullaniciAdi;
    Connection connection;

    // Sekme butonlari
    Button btnTabKonser, btnTabBiletlerim, btnTabFavori, btnTabProfil, btnCikis;

    // --- KONSER SEKMESI ---
    Panel pnlKonser;
    TextField tfArama, tfBiletAdet;
    Checkbox cbPop, cbRap, cbRock, cbArabesk;
    List konserList;
    Button btnAra, btnTumu, btnBiletAl, btnFavEkle;

    // --- BILETLERIM SEKMESI ---
    Panel pnlBiletlerim;
    List  biletList;
    Label lblToplamHarcama;
    Button btnBiletIptal;
    // Aktif / Gecmis filtre
    Checkbox cbAktif, cbGecmis;

    // --- FAVORİLER SEKMESI ---
    Panel pnlFavori;
    List  favoriList;
    Button btnFavSil;
    // Yorum
    TextField tfYorumMetni;
    Choice chPuan;
    Button btnYorumEkle;
    List yorumList;

    // --- PROFİL SEKMESI ---
    Panel pnlProfil;
    Label lblProfilAd, lblProfilKullanici, lblProfilBiletSayisi, lblProfilHarcama;
    TextField tfYeniSifre, tfYeniSifre2;
    Button btnSifreGuncelle;
    Label lblSifreMesaj;

    Panel pnlUst, pnlIcerik;

    public Kullaniciframe(Connection con, int kullaniciID, String kullaniciAdi) {
        super("Konser Takip - Hos geldin, " + kullaniciAdi + "!");
        this.connection   = con;
        this.kullaniciID  = kullaniciID;
        this.kullaniciAdi = kullaniciAdi;

        setSize(660, 720);
        setResizable(false);
        setBackground(KOYU);
        setLayout(new BorderLayout());

        // UST BAR
        pnlUst = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlUst.setBackground(ACIK);
        pnlUst.setPreferredSize(new Dimension(660, 50));

        Label lblHos = new Label("Hos geldin, " + kullaniciAdi + "!", Label.LEFT);
        lblHos.setFont(new Font("Dialog", Font.BOLD, 14)); lblHos.setForeground(ALTIN);

        btnTabKonser   = tabBtn("KONSERLER");
        btnTabBiletlerim=tabBtn("BILETLERIM");
        btnTabFavori   = tabBtn("FAVORİLER");
        btnTabProfil   = tabBtn("PROFİLİM");
        btnCikis       = tabBtn("CIKIS");

        pnlUst.add(lblHos);
        pnlUst.add(btnTabKonser);
        pnlUst.add(btnTabBiletlerim);
        pnlUst.add(btnTabFavori);
        pnlUst.add(btnTabProfil);
        pnlUst.add(btnCikis);
        add(pnlUst, BorderLayout.NORTH);

        // ICERIK
        pnlIcerik = new Panel(new CardLayout());
        pnlIcerik.setBackground(KOYU);

        olusturKonserSekmesi();
        olusturBiletlerimSekmesi();
        olusturFavoriSekmesi();
        olusturProfilSekmesi();

        pnlIcerik.add(pnlKonser,     "konser");
        pnlIcerik.add(pnlBiletlerim, "biletlerim");
        pnlIcerik.add(pnlFavori,     "favori");
        pnlIcerik.add(pnlProfil,     "profil");
        add(pnlIcerik, BorderLayout.CENTER);

        konserleriYukle("", tumTurler());

        Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ekran.width - 660) / 2, (ekran.height - 720) / 2);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new LoginFrame(); }
        });
    }

    private Button tabBtn(String etiket) {
        Button b = new Button(etiket);
        b.setFont(F_BUTON); b.setForeground(SIYAH);
        b.setPreferredSize(new Dimension(100, 30));
        b.addActionListener(this);
        return b;
    }

    private Button aksiyon(String etiket) {
        Button b = new Button(etiket);
        b.setFont(F_BUTON); b.setForeground(SIYAH);
        b.setPreferredSize(new Dimension(155, 32));
        b.addActionListener(this);
        return b;
    }

    private Panel baslikPaneli(String metin, int genislik) {
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        p.setBackground(ACIK);
        p.setPreferredSize(new Dimension(genislik, 36));
        Label l = new Label(metin); l.setFont(F_BASLIK); l.setForeground(ALTIN);
        p.add(l);
        return p;
    }

    // =====================================================================
    //  KONSER SEKMESI
    // =====================================================================
    private void olusturKonserSekmesi() {
        pnlKonser = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 7));
        pnlKonser.setBackground(KOYU);

        // Arama
        Panel pArama = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        pArama.setBackground(new Color(110, 25, 75));
        pArama.setPreferredSize(new Dimension(635, 46));
        Label lAr = new Label("Ara:"); lAr.setFont(F_LABEL); lAr.setForeground(ALTIN);
        tfArama = new TextField(20); stilField(tfArama); tfArama.addKeyListener(this);
        btnAra  = aksiyon("ARA"); btnAra.setPreferredSize(new Dimension(60, 28));
        btnTumu = aksiyon("TUMU"); btnTumu.setPreferredSize(new Dimension(65, 28));
        pArama.add(lAr); pArama.add(tfArama); pArama.add(btnAra); pArama.add(btnTumu);
        pnlKonser.add(pArama);

        // Tur filtresi
        Panel pFiltre = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 7));
        pFiltre.setBackground(ORTA);
        pFiltre.setPreferredSize(new Dimension(635, 42));
        Label lF = new Label("Tur:"); lF.setFont(F_LABEL); lF.setForeground(ALTIN);
        cbPop = cb("Pop"); cbRap = cb("Rap"); cbRock = cb("Rock"); cbArabesk = cb("Arabesk");
        pFiltre.add(lF); pFiltre.add(cbPop); pFiltre.add(cbRap); pFiltre.add(cbRock); pFiltre.add(cbArabesk);
        pnlKonser.add(pFiltre);

        // Konser listesi
        pnlKonser.add(baslikPaneli("Aktif Konserler", 635));
        konserList = new List(11, false);
        konserList.setBackground(new Color(70, 10, 50));
        konserList.setForeground(GRI); konserList.setFont(F_LIST);
        konserList.setPreferredSize(new Dimension(635, 220));
        pnlKonser.add(konserList);

        // Bilet al + Favori
        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 7));
        pAlt.setBackground(ORTA);
        pAlt.setPreferredSize(new Dimension(635, 50));
        Label lAdet = new Label("Bilet Adedi:"); lAdet.setFont(F_LABEL); lAdet.setForeground(GRI);
        tfBiletAdet = new TextField(5); stilField(tfBiletAdet);
        btnBiletAl = aksiyon("BILET SATIN AL");
        btnFavEkle = aksiyon("FAVORIYE EKLE"); btnFavEkle.setPreferredSize(new Dimension(145, 32));
        pAlt.add(lAdet); pAlt.add(tfBiletAdet); pAlt.add(btnBiletAl); pAlt.add(btnFavEkle);
        pnlKonser.add(pAlt);

        // Yorumlar
        pnlKonser.add(baslikPaneli("Secili Konser Yorumlari", 635));
        yorumList = new List(5, false);
        yorumList.setBackground(new Color(70, 10, 50));
        yorumList.setForeground(GRI); yorumList.setFont(F_LIST);
        yorumList.setPreferredSize(new Dimension(635, 100));
        pnlKonser.add(yorumList);

        // Yorum yaz
        Panel pYorum = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        pYorum.setBackground(new Color(110, 25, 75));
        pYorum.setPreferredSize(new Dimension(635, 48));
        Label lPuan = new Label("Puan:"); lPuan.setFont(F_LABEL); lPuan.setForeground(ALTIN);
        chPuan = new Choice(); chPuan.setFont(F_FIELD);
        for (int i = 1; i <= 5; i++) chPuan.add(i + " yildiz");
        Label lYorum = new Label("Yorum:"); lYorum.setFont(F_LABEL); lYorum.setForeground(ALTIN);
        tfYorumMetni = new TextField(20); stilField(tfYorumMetni);
        btnYorumEkle = aksiyon("YORUM EKLE"); btnYorumEkle.setPreferredSize(new Dimension(120, 30));
        pYorum.add(lPuan); pYorum.add(chPuan); pYorum.add(lYorum); pYorum.add(tfYorumMetni); pYorum.add(btnYorumEkle);
        pnlKonser.add(pYorum);

        // Konser secilince yorumlari goster
        konserList.addItemListener(this);
    }

    // =====================================================================
    //  BILETLERIM SEKMESI
    // =====================================================================
    private void olusturBiletlerimSekmesi() {
        pnlBiletlerim = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        pnlBiletlerim.setBackground(KOYU);
        pnlBiletlerim.add(baslikPaneli("Biletlerim", 635));

        // Aktif / Gecmis filtre
        Panel pFil = new Panel(new FlowLayout(FlowLayout.LEFT, 15, 7));
        pFil.setBackground(ORTA);
        pFil.setPreferredSize(new Dimension(635, 40));
        Label lFil = new Label("Filtre:"); lFil.setFont(F_LABEL); lFil.setForeground(ALTIN);
        cbAktif  = new Checkbox("Aktif Konserler",  true);  cbAktif.setFont(F_LABEL);  cbAktif.setForeground(BEYAZ);  cbAktif.setBackground(ORTA);  cbAktif.addItemListener(this);
        cbGecmis = new Checkbox("Gecmis Konserler", true); cbGecmis.setFont(F_LABEL); cbGecmis.setForeground(BEYAZ); cbGecmis.setBackground(ORTA); cbGecmis.addItemListener(this);
        pFil.add(lFil); pFil.add(cbAktif); pFil.add(cbGecmis);
        pnlBiletlerim.add(pFil);

        biletList = new List(18, false);
        biletList.setBackground(new Color(70, 10, 50));
        biletList.setForeground(GRI); biletList.setFont(F_LIST);
        biletList.setPreferredSize(new Dimension(635, 490));
        pnlBiletlerim.add(biletList);

        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pAlt.setBackground(ORTA);
        pAlt.setPreferredSize(new Dimension(635, 50));
        lblToplamHarcama = new Label("Toplam Harcama: 0.00 TL");
        lblToplamHarcama.setFont(new Font("Dialog", Font.BOLD, 13)); lblToplamHarcama.setForeground(ALTIN);
        btnBiletIptal = aksiyon("BILETI IPTAL ET");
        pAlt.add(lblToplamHarcama); pAlt.add(btnBiletIptal);
        pnlBiletlerim.add(pAlt);
    }

    // =====================================================================
    //  FAVORİLER SEKMESI
    // =====================================================================
    private void olusturFavoriSekmesi() {
        pnlFavori = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlFavori.setBackground(KOYU);
        pnlFavori.add(baslikPaneli("Favori Konserlerim", 635));

        favoriList = new List(20, false);
        favoriList.setBackground(new Color(70, 10, 50));
        favoriList.setForeground(GRI); favoriList.setFont(F_LIST);
        favoriList.setPreferredSize(new Dimension(635, 530));
        pnlFavori.add(favoriList);

        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pAlt.setBackground(ORTA);
        pAlt.setPreferredSize(new Dimension(635, 50));
        btnFavSil = aksiyon("FAVORIDEN CIKAR");
        pAlt.add(btnFavSil);
        pnlFavori.add(pAlt);
    }

    // =====================================================================
    //  PROFİL SEKMESI
    // =====================================================================
    private void olusturProfilSekmesi() {
        pnlProfil = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        pnlProfil.setBackground(KOYU);
        pnlProfil.add(baslikPaneli("Profilim", 635));

        // Bilgiler
        Panel pBilgi = new Panel(new GridLayout(4, 1, 0, 10));
        pBilgi.setBackground(ORTA);
        pBilgi.setPreferredSize(new Dimension(635, 130));
        lblProfilAd        = profilLabel("Ad Soyad: -");
        lblProfilKullanici = profilLabel("Kullanici Adi: -");
        lblProfilBiletSayisi=profilLabel("Toplam Bilet: -");
        lblProfilHarcama   = profilLabel("Toplam Harcama: -");
        pBilgi.add(lblProfilAd); pBilgi.add(lblProfilKullanici);
        pBilgi.add(lblProfilBiletSayisi); pBilgi.add(lblProfilHarcama);
        pnlProfil.add(pBilgi);

        // Sifre guncelle
        Panel pSifreBas = baslikPaneli("Sifre Guncelle", 635);
        pnlProfil.add(pSifreBas);

        Panel pSifre = new Panel(new GridLayout(2, 2, 10, 10));
        pSifre.setBackground(ORTA);
        pSifre.setPreferredSize(new Dimension(635, 80));
        Label lS1 = new Label("  Yeni Sifre:"); stilLabel(lS1);
        tfYeniSifre = new TextField(); tfYeniSifre.setEchoChar('*'); stilField(tfYeniSifre);
        Label lS2 = new Label("  Sifre Tekrar:"); stilLabel(lS2);
        tfYeniSifre2 = new TextField(); tfYeniSifre2.setEchoChar('*'); stilField(tfYeniSifre2);
        pSifre.add(lS1); pSifre.add(tfYeniSifre);
        pSifre.add(lS2); pSifre.add(tfYeniSifre2);
        pnlProfil.add(pSifre);

        lblSifreMesaj = new Label("", Label.CENTER);
        lblSifreMesaj.setFont(new Font("Dialog", Font.BOLD, 12));
        lblSifreMesaj.setForeground(YESIL);
        lblSifreMesaj.setPreferredSize(new Dimension(635, 22));
        pnlProfil.add(lblSifreMesaj);

        btnSifreGuncelle = aksiyon("SİFREYİ GUNCELLE");
        btnSifreGuncelle.addActionListener(this);
        pnlProfil.add(btnSifreGuncelle);
    }

    private Label profilLabel(String metin) {
        Label l = new Label("  " + metin, Label.LEFT);
        l.setFont(new Font("Dialog", Font.BOLD, 14));
        l.setForeground(GRI);
        return l;
    }

    // =====================================================================
    //  LISTENERS
    // =====================================================================
    public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) pnlIcerik.getLayout();
        Object k = e.getSource();

        if      (k == btnTabKonser)    { cl.show(pnlIcerik, "konser");     konserleriYukle(tfArama.getText().trim(), seciliTurler()); }
        else if (k == btnTabBiletlerim){ cl.show(pnlIcerik, "biletlerim"); biletleriYukle(); }
        else if (k == btnTabFavori)    { cl.show(pnlIcerik, "favori");     favorileriYukle(); }
        else if (k == btnTabProfil)    { cl.show(pnlIcerik, "profil");     profilYukle(); }
        else if (k == btnCikis)        { dispose(); new LoginFrame(); }
        else if (k == btnBiletAl)      biletSatinAl();
        else if (k == btnFavEkle)      favoriEkle();
        else if (k == btnFavSil)       favoriSil();
        else if (k == btnBiletIptal)   biletIptalEt();
        else if (k == btnYorumEkle)    yorumEkle();
        else if (k == btnSifreGuncelle)sifreGuncelle();
        else if (e.getActionCommand().equals("ARA"))  konserleriYukle(tfArama.getText().trim(), seciliTurler());
        else if (e.getActionCommand().equals("TUMU")) { tfArama.setText(""); konserleriYukle("", tumTurler()); }
    }

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (src == cbPop || src == cbRap || src == cbRock || src == cbArabesk)
            konserleriYukle(tfArama.getText().trim(), seciliTurler());
        else if (src == cbAktif || src == cbGecmis)
            biletleriYukle();
        else if (src == konserList)
            yorumlariYukle();
    }

    public void keyReleased(KeyEvent e) { konserleriYukle(tfArama.getText().trim(), seciliTurler()); }
    public void keyPressed(KeyEvent e)  {}
    public void keyTyped(KeyEvent e)    {}

    // =====================================================================
    //  YARDIMCI
    // =====================================================================
    private Checkbox cb(String etiket) {
        Checkbox c = new Checkbox(etiket, true);
        c.setFont(new Font("Dialog", Font.BOLD, 12));
        c.setForeground(BEYAZ); c.setBackground(ORTA);
        c.addItemListener(this);
        return c;
    }

    private String[] seciliTurler() {
        java.util.List<String> l = new java.util.ArrayList<>();
        if (cbPop.getState()) l.add("Pop"); if (cbRap.getState()) l.add("Rap");
        if (cbRock.getState()) l.add("Rock"); if (cbArabesk.getState()) l.add("Arabesk");
        return l.toArray(new String[0]);
    }

    private String[] tumTurler() { return new String[]{"Pop","Rap","Rock","Arabesk"}; }

    private void stilLabel(Label l) { l.setFont(F_LABEL); l.setForeground(GRI); }
    private void stilField(TextField tf) {
        tf.setFont(F_FIELD); tf.setBackground(new Color(70, 10, 50)); tf.setForeground(BEYAZ);
    }

    private int seciliKonserID() {
        String s = konserList.getSelectedItem();
        if (s == null) return -1;
        try { return Integer.parseInt(s.split("\\|")[0].replace("#","").trim()); }
        catch (Exception e) { return -1; }
    }

    // =====================================================================
    //  VERİTABANI
    // =====================================================================
    private void konserleriYukle(String anahtar, String[] turler) {
        konserList.removeAll();
        if (turler.length == 0) return;
        try {
            StringBuilder inC = new StringBuilder();
            for (int i = 0; i < turler.length; i++) { inC.append("?"); if (i < turler.length-1) inC.append(","); }
            // Sadece gelecek / bugunun konserlerini goster (aktif)
            String sql = "SELECT id,ad,tarih,konum,fiyat,tur,kapasite,satilan FROM Konser WHERE tur IN (" + inC + ")";
            if (anahtar != null && !anahtar.isEmpty())
                sql += " AND (ad LIKE ? OR konum LIKE ? OR tarih LIKE ?)";
            sql += " ORDER BY tarih ASC";
            PreparedStatement ps = connection.prepareStatement(sql);
            int idx = 1;
            for (String t : turler) ps.setString(idx++, t);
            if (anahtar != null && !anahtar.isEmpty()) {
                String like = "%" + anahtar + "%";
                ps.setString(idx++, like); ps.setString(idx++, like); ps.setString(idx, like);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int kap = rs.getInt("kapasite"), sat = rs.getInt("satilan");
                String doluluk = kap > 0 ? String.format("%.0f%%", sat * 100.0 / kap) : "-";
                konserList.add(String.format("#%d | %-18s | %s | %-12s | %.0f TL | %s | Doluluk:%s",
                        rs.getInt("id"), rs.getString("ad"), rs.getString("tarih"),
                        rs.getString("konum"), rs.getDouble("fiyat"), rs.getString("tur"), doluluk));
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void biletSatinAl() {
        String secili = konserList.getSelectedItem();
        if (secili == null) return;
        String adetStr = tfBiletAdet.getText().trim();
        if (adetStr.isEmpty()) return;
        try {
            String[] p = secili.split("\\|");
            int    kID  = Integer.parseInt(p[0].replace("#","").trim());
            String ad   = p[1].trim(), tarih = p[2].trim(), konum = p[3].trim(), tur = p[5].trim();
            double fiyat= Double.parseDouble(p[4].replace("TL","").trim());
            int    adet = Integer.parseInt(adetStr);

            // Kapasite kontrolu
            ResultSet krs = connection.createStatement().executeQuery(
                    "SELECT kapasite, satilan FROM Konser WHERE id=" + kID);
            if (krs.next()) {
                int kalan = krs.getInt("kapasite") - krs.getInt("satilan");
                if (adet > kalan) {
                    Dialog uyari = new Dialog(this, "Kapasite Dolu!", true);
                    uyari.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
                    uyari.setSize(300, 130); uyari.setBackground(ORTA);
                    Label lbl = new Label("Yeterli kapasite yok! Kalan: " + kalan);
                    lbl.setForeground(BEYAZ); lbl.setFont(F_LABEL);
                    Button ok = new Button("TAMAM"); ok.setForeground(SIYAH); ok.setFont(F_BUTON);
                    ok.addActionListener(ev -> uyari.dispose());
                    uyari.add(lbl); uyari.add(ok);
                    Dimension ekr = Toolkit.getDefaultToolkit().getScreenSize();
                    uyari.setLocation((ekr.width-300)/2, (ekr.height-130)/2);
                    uyari.setVisible(true);
                    return;
                }
            }

            double top = adet * fiyat;
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Bilet (kullanici_id,konser_id,konser_ad,konser_tarih,konser_konum,konser_tur,adet,birim_fiyat,toplam,satin_alma_tarihi) VALUES (?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1,kullaniciID); ps.setInt(2,kID); ps.setString(3,ad); ps.setString(4,tarih);
            ps.setString(5,konum); ps.setString(6,tur); ps.setInt(7,adet);
            ps.setDouble(8,fiyat); ps.setDouble(9,top);
            ps.setString(10, java.time.LocalDate.now().toString());
            ps.executeUpdate();

            // Satilan guncelle
            PreparedStatement up = connection.prepareStatement(
                    "UPDATE Konser SET satilan = satilan + ? WHERE id=?");
            up.setInt(1,adet); up.setInt(2,kID); up.executeUpdate();

            tfBiletAdet.setText("");
            konserleriYukle(tfArama.getText().trim(), seciliTurler());

            // Onay dialog
            Dialog d = new Dialog(this, "Bilet Alindi!", true);
            d.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
            d.setSize(320, 165); d.setBackground(ORTA);
            Label l1 = new Label("Konser: " + ad); l1.setForeground(ALTIN); l1.setFont(F_LABEL);
            Label l2 = new Label(tur + " | " + tarih + " | " + konum); l2.setForeground(GRI); l2.setFont(F_LABEL);
            Label l3 = new Label(adet + " bilet | Toplam: " + top + " TL"); l3.setForeground(BEYAZ); l3.setFont(new Font("Dialog",Font.BOLD,14));
            Button ok = new Button("TAMAM"); ok.setFont(F_BUTON); ok.setForeground(SIYAH);
            ok.setPreferredSize(new Dimension(100,30)); ok.addActionListener(ev -> d.dispose());
            d.add(l1); d.add(l2); d.add(l3); d.add(ok);
            Dimension ekr = Toolkit.getDefaultToolkit().getScreenSize();
            d.setLocation((ekr.width-320)/2,(ekr.height-165)/2); d.setVisible(true);

        } catch (Exception ex) { System.out.println("Bilet hatasi: " + ex.getMessage()); }
    }

    private void biletleriYukle() {
        biletList.removeAll();
        double toplam = 0;
        try {
            String bugun = java.time.LocalDate.now().toString();
            String sql = "SELECT b.id, b.konser_ad, b.konser_tur, b.konser_tarih, b.adet, b.toplam, b.satin_alma_tarihi " +
                    "FROM Bilet b WHERE b.kullanici_id=?";
            java.util.List<String> kosullar = new java.util.ArrayList<>();
            if (cbAktif.getState() && !cbGecmis.getState())
                kosullar.add("b.konser_tarih >= '" + bugun + "'");
            else if (!cbAktif.getState() && cbGecmis.getState())
                kosullar.add("b.konser_tarih < '" + bugun + "'");
            if (!kosullar.isEmpty()) sql += " AND " + kosullar.get(0);
            sql += " ORDER BY b.id DESC";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, kullaniciID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double t = rs.getDouble("toplam");
                toplam += t;
                String tarih = rs.getString("konser_tarih");
                boolean gecmis = tarih.compareTo(bugun) < 0;
                biletList.add(String.format("%s #%d | %s | %s | %s | %d bilet | %.2f TL | Alim:%s",
                        gecmis ? "[GECMIS]" : "[AKTIF] ",
                        rs.getInt("id"), rs.getString("konser_ad"), rs.getString("konser_tur"),
                        tarih, rs.getInt("adet"), t, rs.getString("satin_alma_tarihi")));
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        lblToplamHarcama.setText("Toplam Harcama: " + String.format("%.2f", toplam) + " TL");
    }

    private void biletIptalEt() {
        String s = biletList.getSelectedItem(); if (s == null) return;
        try {
            int id = Integer.parseInt(s.replaceAll(".*#(\\d+)\\|.*","$1").trim());
            PreparedStatement getPs = connection.prepareStatement(
                    "SELECT konser_id, adet FROM Bilet WHERE id=? AND kullanici_id=?");
            getPs.setInt(1,id); getPs.setInt(2,kullaniciID);
            ResultSet rs = getPs.executeQuery();
            if (rs.next()) {
                PreparedStatement up = connection.prepareStatement(
                        "UPDATE Konser SET satilan=satilan-? WHERE id=?");
                up.setInt(1,rs.getInt("adet")); up.setInt(2,rs.getInt("konser_id")); up.executeUpdate();
            }
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM Bilet WHERE id=? AND kullanici_id=?");
            ps.setInt(1,id); ps.setInt(2,kullaniciID); ps.executeUpdate();
            biletleriYukle();
        } catch (Exception ex) { System.out.println("Bilet iptal: " + ex.getMessage()); }
    }

    private void favoriEkle() {
        int kID = seciliKonserID(); if (kID == -1) return;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR IGNORE INTO Favori (kullanici_id, konser_id) VALUES (?,?)");
            ps.setInt(1,kullaniciID); ps.setInt(2,kID); ps.executeUpdate();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void favorileriYukle() {
        favoriList.removeAll();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT k.id,k.ad,k.tarih,k.konum,k.fiyat,k.tur FROM Favori f " +
                            "JOIN Konser k ON f.konser_id=k.id WHERE f.kullanici_id=? ORDER BY k.tarih");
            ps.setInt(1, kullaniciID);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                favoriList.add(String.format("#%d | %-18s | %s | %-12s | %.0f TL | %s",
                        rs.getInt("id"), rs.getString("ad"), rs.getString("tarih"),
                        rs.getString("konum"), rs.getDouble("fiyat"), rs.getString("tur")));
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void favoriSil() {
        String s = favoriList.getSelectedItem(); if (s == null) return;
        try {
            int kID = Integer.parseInt(s.split("\\|")[0].replace("#","").trim());
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM Favori WHERE kullanici_id=? AND konser_id=?");
            ps.setInt(1,kullaniciID); ps.setInt(2,kID); ps.executeUpdate();
            favorileriYukle();
        } catch (Exception ex) { System.out.println(ex.getMessage()); }
    }

    private void yorumEkle() {
        int kID = seciliKonserID(); if (kID == -1) return;
        String metin = tfYorumMetni.getText().trim(); if (metin.isEmpty()) return;
        int puan = chPuan.getSelectedIndex() + 1;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Yorum (kullanici_id,konser_id,puan,yorum_metni,tarih) VALUES (?,?,?,?,?)");
            ps.setInt(1,kullaniciID); ps.setInt(2,kID); ps.setInt(3,puan);
            ps.setString(4,metin); ps.setString(5,java.time.LocalDate.now().toString());
            ps.executeUpdate();
            tfYorumMetni.setText("");
            yorumlariYukle();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void yorumlariYukle() {
        yorumList.removeAll();
        int kID = seciliKonserID(); if (kID == -1) return;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT k.kullanici_adi, y.puan, y.yorum_metni, y.tarih FROM Yorum y " +
                            "JOIN Kullanici k ON y.kullanici_id=k.id WHERE y.konser_id=? ORDER BY y.id DESC");
            ps.setInt(1,kID);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                yorumList.add(String.format("[%d yildiz] @%s: %s (%s)",
                        rs.getInt("puan"), rs.getString("kullanici_adi"),
                        rs.getString("yorum_metni"), rs.getString("tarih")));
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void profilYukle() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT ad, soyad, kullanici_adi FROM Kullanici WHERE id=?");
            ps.setInt(1,kullaniciID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblProfilAd.setText("  Ad Soyad: " + rs.getString("ad") + " " + rs.getString("soyad"));
                lblProfilKullanici.setText("  Kullanici Adi: @" + rs.getString("kullanici_adi"));
            }
            PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT COUNT(*), COALESCE(SUM(toplam),0) FROM Bilet WHERE kullanici_id=?");
            ps2.setInt(1,kullaniciID);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                lblProfilBiletSayisi.setText("  Toplam Bilet: " + rs2.getInt(1));
                lblProfilHarcama.setText("  Toplam Harcama: " + String.format("%.2f", rs2.getDouble(2)) + " TL");
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void sifreGuncelle() {
        String s1 = tfYeniSifre.getText().trim(), s2 = tfYeniSifre2.getText().trim();
        if (s1.isEmpty() || s2.isEmpty()) { lblSifreMesaj.setText("Sifre bos olamaz!"); lblSifreMesaj.setForeground(new Color(220,80,80)); return; }
        if (!s1.equals(s2)) { lblSifreMesaj.setText("Sifreler uyusmuyor!"); lblSifreMesaj.setForeground(new Color(220,80,80)); return; }
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Kullanici SET sifre=? WHERE id=?");
            ps.setString(1,s1); ps.setInt(2,kullaniciID); ps.executeUpdate();
            lblSifreMesaj.setText("Sifre basariyla guncellendi!"); lblSifreMesaj.setForeground(YESIL);
            tfYeniSifre.setText(""); tfYeniSifre2.setText("");
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }
}
