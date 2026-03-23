package konsertakip;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class KonserTakipSistemi extends Frame implements ActionListener, KeyListener, ItemListener {

    static final Color KOYU_MOR  = new Color(90, 20, 60);
    static final Color ACIK_MOR  = new Color(180, 50, 110);
    static final Color ALTIN     = new Color(255, 200, 50);
    static final Color BEYAZ     = new Color(255, 255, 255);
    static final Color GRI       = new Color(220, 215, 230);
    static final Color SIYAH     = new Color(0, 0, 0);

    static final Font FONT_BASLIK = new Font("Dialog", Font.BOLD, 20);
    static final Font FONT_LABEL  = new Font("Dialog", Font.BOLD, 13);
    static final Font FONT_FIELD  = new Font("Dialog", Font.PLAIN, 13);
    static final Font FONT_BUTON  = new Font("Dialog", Font.BOLD, 13);
    static final Font FONT_LIST   = new Font("Monospaced", Font.PLAIN, 12);
    static final Font FONT_CHECK  = new Font("Dialog", Font.BOLD, 12);

    // Tur secenekleri
    static final String[] TURLER = {"Pop", "Rap", "Rock", "Arabesk"};

    // Ana ekran
    Label     lblBaslik, lblAd, lblTarih, lblKonum, lblFiyat, lblBiletAdet, lblArama, lblTur;
    TextField tfAd, tfTarih, tfKonum, tfFiyat, tfBiletAdet, tfArama;
    Choice    chTur;   // konser eklerken tur secimi
    Checkbox  cbPop, cbRap, cbRock, cbArabesk;  // filtre checkboxlari
    Button    btnEkle, btnSil, btnBiletAl, btnAra, btnTumunuGoster, btnBiletleriGoster;
    List      konserList;

    // Bilet ekrani
    List   biletList;
    Button btnBiletSil, btnKonserlereGeri;
    Label  lblBiletBaslik, lblToplamTutar;

    // Panel'ler (tekrar kullanilacak)
    Panel pnlBaslik, pnlArama, pnlFiltre, pnlForm, pnlButonlar, pnlListe, pnlBiletAl;
    Panel pnlBiletEkrani;

    MenuBar  menuBar;
    Menu     menuDosya;
    MenuItem miCikis;

    Connection connection;
    static final String DB_URL = "jdbc:sqlite:konser.db";

    public KonserTakipSistemi() {
        super("Konser Takip Sistemi");

        baglantiKur();
        tabloOlustur();

        setSize(580, 820);
        setResizable(false);
        setBackground(KOYU_MOR);
        setLayout(new BorderLayout());

        menuBar   = new MenuBar();
        menuDosya = new Menu("Dosya");
        miCikis   = new MenuItem("Cikis");
        miCikis.addActionListener(this);
        menuDosya.add(miCikis);
        menuBar.add(menuDosya);
        setMenuBar(menuBar);

        olusturBiletEkrani();
        add(olusturAnaEkran(), BorderLayout.CENTER);

        konserleriYukle("", tumTurler());

        Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ekran.width - 580) / 2, (ekran.height - 820) / 2);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { baglantiKapat(); dispose(); }
        });
    }

    // -------------------------------------------------------
    //  ANA EKRAN OLUSTUR
    // -------------------------------------------------------
    private Panel olusturAnaEkran() {
        Panel pnl = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 7));
        pnl.setBackground(KOYU_MOR);

        // BASLIK
        pnlBaslik = new Panel(new FlowLayout(FlowLayout.CENTER));
        pnlBaslik.setBackground(ACIK_MOR);
        pnlBaslik.setPreferredSize(new Dimension(560, 50));
        lblBaslik = new Label("*** Konser Takip Sistemi ***", Label.CENTER);
        lblBaslik.setFont(FONT_BASLIK);
        lblBaslik.setForeground(ALTIN);
        pnlBaslik.add(lblBaslik);
        pnl.add(pnlBaslik);

        // SEKME BUTONU
        Panel pnlSekme = new Panel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlSekme.setBackground(KOYU_MOR);
        pnlSekme.setPreferredSize(new Dimension(560, 38));
        btnBiletleriGoster = new Button(">> BILETLERIM");
        btnBiletleriGoster.setFont(FONT_BUTON);
        btnBiletleriGoster.setForeground(SIYAH);
        btnBiletleriGoster.setPreferredSize(new Dimension(155, 28));
        btnBiletleriGoster.addActionListener(this);
        pnlSekme.add(btnBiletleriGoster);
        pnl.add(pnlSekme);

        // ARAMA
        pnlArama = new Panel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        pnlArama.setBackground(new Color(110, 25, 75));
        pnlArama.setPreferredSize(new Dimension(560, 48));
        lblArama = new Label("Konser Ara:");
        lblArama.setFont(FONT_LABEL); lblArama.setForeground(ALTIN);
        tfArama = new TextField(18); stilField(tfArama);
        tfArama.addKeyListener(this);
        btnAra = new Button("ARA");
        btnAra.setFont(FONT_BUTON); btnAra.setForeground(SIYAH);
        btnAra.setPreferredSize(new Dimension(65, 28)); btnAra.addActionListener(this);
        btnTumunuGoster = new Button("TUMU");
        btnTumunuGoster.setFont(FONT_BUTON); btnTumunuGoster.setForeground(SIYAH);
        btnTumunuGoster.setPreferredSize(new Dimension(65, 28)); btnTumunuGoster.addActionListener(this);
        pnlArama.add(lblArama); pnlArama.add(tfArama);
        pnlArama.add(btnAra);   pnlArama.add(btnTumunuGoster);
        pnl.add(pnlArama);

        // TUR FILTRESI (Checkbox)
        pnlFiltre = new Panel(new FlowLayout(FlowLayout.LEFT, 12, 7));
        pnlFiltre.setBackground(new Color(130, 35, 85));
        pnlFiltre.setPreferredSize(new Dimension(560, 45));
        Label lblFiltre = new Label("Tur Filtrele:");
        lblFiltre.setFont(FONT_LABEL); lblFiltre.setForeground(ALTIN);

        cbPop     = yeniCheckbox("Pop");
        cbRap     = yeniCheckbox("Rap");
        cbRock    = yeniCheckbox("Rock");
        cbArabesk = yeniCheckbox("Arabesk");

        pnlFiltre.add(lblFiltre);
        pnlFiltre.add(cbPop);
        pnlFiltre.add(cbRap);
        pnlFiltre.add(cbRock);
        pnlFiltre.add(cbArabesk);
        pnl.add(pnlFiltre);

        // FORM (tur secimi dahil — 5 satir)
        pnlForm = new Panel(new GridLayout(5, 2, 10, 6));
        pnlForm.setBackground(new Color(140, 40, 90));
        pnlForm.setPreferredSize(new Dimension(560, 170));

        lblAd = new Label("  Konser Adi:"); stilLabel(lblAd);
        tfAd  = new TextField();           stilField(tfAd);
        pnlForm.add(lblAd); pnlForm.add(tfAd);

        lblTarih = new Label("  Tarih (GG/AA/YYYY):"); stilLabel(lblTarih);
        tfTarih  = new TextField();                     stilField(tfTarih);
        pnlForm.add(lblTarih); pnlForm.add(tfTarih);

        lblKonum = new Label("  Konum:"); stilLabel(lblKonum);
        tfKonum  = new TextField();        stilField(tfKonum);
        pnlForm.add(lblKonum); pnlForm.add(tfKonum);

        lblFiyat = new Label("  Bilet Fiyati (TL):"); stilLabel(lblFiyat);
        tfFiyat  = new TextField();                    stilField(tfFiyat);
        pnlForm.add(lblFiyat); pnlForm.add(tfFiyat);

        lblTur = new Label("  Tur:"); stilLabel(lblTur);
        chTur  = new Choice();
        chTur.setFont(FONT_FIELD);
        for (String t : TURLER) chTur.add(t);
        pnlForm.add(lblTur); pnlForm.add(chTur);
        pnl.add(pnlForm);

        // EKLE / SIL
        pnlButonlar = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 7));
        pnlButonlar.setBackground(KOYU_MOR);
        pnlButonlar.setPreferredSize(new Dimension(560, 50));
        btnEkle = new Button("+ KONSER EKLE");
        btnEkle.setFont(FONT_BUTON); btnEkle.setForeground(SIYAH);
        btnEkle.setPreferredSize(new Dimension(170, 34)); btnEkle.addActionListener(this);
        btnSil = new Button("- KONSER SIL");
        btnSil.setFont(FONT_BUTON); btnSil.setForeground(SIYAH);
        btnSil.setPreferredSize(new Dimension(170, 34)); btnSil.addActionListener(this);
        pnlButonlar.add(btnEkle); pnlButonlar.add(btnSil);
        pnl.add(pnlButonlar);

        // KONSER LISTESI
        pnlListe = new Panel(new BorderLayout(0, 4));
        pnlListe.setBackground(KOYU_MOR);
        pnlListe.setPreferredSize(new Dimension(560, 175));
        Label lblListe = new Label("  Kayitli Konserler:", Label.LEFT);
        lblListe.setFont(new Font("Dialog", Font.BOLD, 13)); lblListe.setForeground(ALTIN);
        konserList = new List(7, false);
        konserList.setBackground(new Color(80, 15, 55));
        konserList.setForeground(GRI); konserList.setFont(FONT_LIST);
        pnlListe.add(lblListe,   BorderLayout.NORTH);
        pnlListe.add(konserList, BorderLayout.CENTER);
        pnl.add(pnlListe);

        // BILET AL
        pnlBiletAl = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 7));
        pnlBiletAl.setBackground(new Color(140, 40, 90));
        pnlBiletAl.setPreferredSize(new Dimension(560, 50));
        lblBiletAdet = new Label("Bilet Adedi:"); stilLabel(lblBiletAdet);
        tfBiletAdet  = new TextField(6);           stilField(tfBiletAdet);
        btnBiletAl = new Button("BILET SATIN AL");
        btnBiletAl.setFont(FONT_BUTON); btnBiletAl.setForeground(SIYAH);
        btnBiletAl.setPreferredSize(new Dimension(170, 34)); btnBiletAl.addActionListener(this);
        pnlBiletAl.add(lblBiletAdet); pnlBiletAl.add(tfBiletAdet); pnlBiletAl.add(btnBiletAl);
        pnl.add(pnlBiletAl);

        return pnl;
    }

    // -------------------------------------------------------
    //  BILET EKRANI OLUSTUR
    // -------------------------------------------------------
    private void olusturBiletEkrani() {
        pnlBiletEkrani = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlBiletEkrani.setBackground(KOYU_MOR);

        Panel pnlBBaslik = new Panel(new FlowLayout(FlowLayout.CENTER));
        pnlBBaslik.setBackground(ACIK_MOR);
        pnlBBaslik.setPreferredSize(new Dimension(560, 50));
        lblBiletBaslik = new Label("*** Satin Alinan Biletler ***", Label.CENTER);
        lblBiletBaslik.setFont(FONT_BASLIK); lblBiletBaslik.setForeground(ALTIN);
        pnlBBaslik.add(lblBiletBaslik);
        pnlBiletEkrani.add(pnlBBaslik);

        Panel pnlGeri = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 7));
        pnlGeri.setBackground(KOYU_MOR);
        pnlGeri.setPreferredSize(new Dimension(560, 42));
        btnKonserlereGeri = new Button("<< KONSERLERE DON");
        btnKonserlereGeri.setFont(FONT_BUTON); btnKonserlereGeri.setForeground(SIYAH);
        btnKonserlereGeri.setPreferredSize(new Dimension(195, 28));
        btnKonserlereGeri.addActionListener(this);
        pnlGeri.add(btnKonserlereGeri);
        pnlBiletEkrani.add(pnlGeri);

        Panel pnlBListe = new Panel(new BorderLayout(0, 4));
        pnlBListe.setBackground(KOYU_MOR);
        pnlBListe.setPreferredSize(new Dimension(560, 530));
        Label lblBL = new Label("  Biletlerim:", Label.LEFT);
        lblBL.setFont(new Font("Dialog", Font.BOLD, 13)); lblBL.setForeground(ALTIN);
        biletList = new List(20, false);
        biletList.setBackground(new Color(80, 15, 55));
        biletList.setForeground(GRI); biletList.setFont(FONT_LIST);
        pnlBListe.add(lblBL,      BorderLayout.NORTH);
        pnlBListe.add(biletList,  BorderLayout.CENTER);
        pnlBiletEkrani.add(pnlBListe);

        Panel pnlBAlt = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        pnlBAlt.setBackground(new Color(140, 40, 90));
        pnlBAlt.setPreferredSize(new Dimension(560, 55));
        lblToplamTutar = new Label("Toplam Harcama: 0.00 TL");
        lblToplamTutar.setFont(new Font("Dialog", Font.BOLD, 13)); lblToplamTutar.setForeground(ALTIN);
        btnBiletSil = new Button("BILETI IPTAL ET");
        btnBiletSil.setFont(FONT_BUTON); btnBiletSil.setForeground(SIYAH);
        btnBiletSil.setPreferredSize(new Dimension(170, 34)); btnBiletSil.addActionListener(this);
        pnlBAlt.add(lblToplamTutar); pnlBAlt.add(btnBiletSil);
        pnlBiletEkrani.add(pnlBAlt);
    }

    // -------------------------------------------------------
    //  YARDIMCI
    // -------------------------------------------------------
    private Checkbox yeniCheckbox(String etiket) {
        Checkbox cb = new Checkbox(etiket, true);
        cb.setFont(FONT_CHECK);
        cb.setForeground(BEYAZ);
        cb.setBackground(new Color(130, 35, 85));
        cb.addItemListener(this);
        return cb;
    }

    private void stilLabel(Label l) { l.setFont(FONT_LABEL); l.setForeground(GRI); }
    private void stilField(TextField tf) {
        tf.setFont(FONT_FIELD);
        tf.setBackground(new Color(70, 10, 50));
        tf.setForeground(BEYAZ);
    }

    // Secili checkbox'lardan tur listesi uret
    private String[] seciliTurler() {
        java.util.List<String> liste = new java.util.ArrayList<>();
        if (cbPop.getState())     liste.add("Pop");
        if (cbRap.getState())     liste.add("Rap");
        if (cbRock.getState())    liste.add("Rock");
        if (cbArabesk.getState()) liste.add("Arabesk");
        return liste.toArray(new String[0]);
    }

    private String[] tumTurler() { return TURLER; }

    // -------------------------------------------------------
    //  EKRAN GECISI
    // -------------------------------------------------------
    private void biletEkraniGoster() {
        removeAll();
        add(pnlBiletEkrani, BorderLayout.CENTER);
        biletleriYukle();
        validate(); repaint();
    }

    private void konserEkraniGoster() {
        removeAll();
        add(olusturAnaEkran(), BorderLayout.CENTER);
        konserleriYukle(tfArama != null ? tfArama.getText().trim() : "", seciliTurler());
        validate(); repaint();
    }

    // -------------------------------------------------------
    //  LISTENER'LAR
    // -------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        Object k = e.getSource();
        if      (k == btnEkle)            konserEkle();
        else if (k == btnSil)             konserSil();
        else if (k == btnBiletAl)         biletSatinAl();
        else if (k == btnAra)             konserleriYukle(tfArama.getText().trim(), seciliTurler());
        else if (k == btnTumunuGoster)    { tfArama.setText(""); konserleriYukle("", tumTurler()); }
        else if (k == btnBiletleriGoster) biletEkraniGoster();
        else if (k == btnKonserlereGeri)  konserEkraniGoster();
        else if (k == btnBiletSil)        biletIptalEt();
        else if (k == miCikis)            { baglantiKapat(); dispose(); }
    }

    // Checkbox degisince otomatik filtrele
    public void itemStateChanged(ItemEvent e) {
        if (tfArama != null)
            konserleriYukle(tfArama.getText().trim(), seciliTurler());
    }

    public void keyReleased(KeyEvent e) { konserleriYukle(tfArama.getText().trim(), seciliTurler()); }
    public void keyPressed(KeyEvent e)  {}
    public void keyTyped(KeyEvent e)    {}

    // -------------------------------------------------------
    //  VERITABANI
    // -------------------------------------------------------
    private void baglantiKur() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception ex) { System.out.println("Baglanti hatasi: " + ex.getMessage()); }
    }

    private void tabloOlustur() {
        try {
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS Konser (" +
                    "id    INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ad    TEXT NOT NULL, " +
                    "tarih TEXT NOT NULL, " +
                    "konum TEXT NOT NULL, " +
                    "fiyat REAL NOT NULL, " +
                    "tur   TEXT NOT NULL DEFAULT 'Pop')");
            // Eski tabloya tur sutunu yoksa ekle
            try { st.execute("ALTER TABLE Konser ADD COLUMN tur TEXT NOT NULL DEFAULT 'Pop'"); }
            catch (SQLException ignore) {}
            st.execute("CREATE TABLE IF NOT EXISTS Bilet (" +
                    "id                INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "konser_id         INTEGER NOT NULL, " +
                    "konser_ad         TEXT    NOT NULL, " +
                    "konser_tarih      TEXT    NOT NULL, " +
                    "konser_konum      TEXT    NOT NULL, " +
                    "konser_tur        TEXT    NOT NULL, " +
                    "adet              INTEGER NOT NULL, " +
                    "birim_fiyat       REAL    NOT NULL, " +
                    "toplam            REAL    NOT NULL, " +
                    "satin_alma_tarihi TEXT    NOT NULL)");
        } catch (SQLException ex) { System.out.println("Tablo hatasi: " + ex.getMessage()); }
    }

    private void baglantiKapat() {
        try { if (connection != null) connection.close(); }
        catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    // -------------------------------------------------------
    //  KONSER ISLEMLERI
    // -------------------------------------------------------
    private void konserleriYukle(String anahtar, String[] turler) {
        konserList.removeAll();
        if (turler.length == 0) return; // hicbir tur secili degilse bos goster

        try {
            // Tur IN (...) kosulunu dinamik olustur
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < turler.length; i++) {
                inClause.append("?");
                if (i < turler.length - 1) inClause.append(",");
            }

            String sql = "SELECT id, ad, tarih, konum, fiyat, tur FROM Konser WHERE tur IN (" +
                    inClause + ")";
            if (anahtar != null && !anahtar.isEmpty()) {
                sql += " AND (ad LIKE ? OR konum LIKE ? OR tarih LIKE ?)";
            }
            sql += " ORDER BY id";

            PreparedStatement ps = connection.prepareStatement(sql);
            int idx = 1;
            for (String t : turler) ps.setString(idx++, t);
            if (anahtar != null && !anahtar.isEmpty()) {
                String like = "%" + anahtar + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx,   like);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                konserList.add(
                        rs.getInt("id")        + "|" +
                                rs.getString("ad")     + "|" +
                                rs.getString("tarih")  + "|" +
                                rs.getString("konum")  + "|" +
                                rs.getDouble("fiyat")  + "|" +
                                rs.getString("tur"));
            }
        } catch (SQLException ex) { System.out.println("Yukleme hatasi: " + ex.getMessage()); }
    }

    private void konserEkle() {
        String ad = tfAd.getText().trim(), tarih = tfTarih.getText().trim(),
                konum = tfKonum.getText().trim(), fiyatStr = tfFiyat.getText().trim();
        String tur = chTur.getSelectedItem();
        if (ad.isEmpty() || tarih.isEmpty() || konum.isEmpty() || fiyatStr.isEmpty()) {
            System.out.println("Tum alanlari doldurun."); return;
        }
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Konser (ad, tarih, konum, fiyat, tur) VALUES (?,?,?,?,?)");
            ps.setString(1, ad); ps.setString(2, tarih);
            ps.setString(3, konum); ps.setDouble(4, Double.parseDouble(fiyatStr));
            ps.setString(5, tur);
            ps.executeUpdate();
            tfAd.setText(""); tfTarih.setText(""); tfKonum.setText(""); tfFiyat.setText("");
            konserleriYukle(tfArama.getText().trim(), seciliTurler());
        } catch (SQLException ex) { System.out.println("Ekleme hatasi: " + ex.getMessage()); }
    }

    private void konserSil() {
        String secili = konserList.getSelectedItem();
        if (secili == null) return;
        try {
            int id = Integer.parseInt(secili.split("\\|")[0].trim());
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Konser WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate();
            konserleriYukle(tfArama.getText().trim(), seciliTurler());
        } catch (Exception ex) { System.out.println("Silme hatasi: " + ex.getMessage()); }
    }

    private void biletSatinAl() {
        String secili = konserList.getSelectedItem();
        if (secili == null) { System.out.println("Konser secin."); return; }
        String adetStr = tfBiletAdet.getText().trim();
        if (adetStr.isEmpty()) { System.out.println("Adet girin."); return; }
        try {
            String[] p = secili.split("\\|");
            int    konserID = Integer.parseInt(p[0].trim());
            String konserAd = p[1].trim(), tarih = p[2].trim();
            String konum    = p[3].trim(), tur   = p[5].trim();
            double fiyat    = Double.parseDouble(p[4].trim());
            int    adet     = Integer.parseInt(adetStr);
            double toplam   = adet * fiyat;

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Bilet (konser_id,konser_ad,konser_tarih,konser_konum," +
                            "konser_tur,adet,birim_fiyat,toplam,satin_alma_tarihi) VALUES (?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, konserID); ps.setString(2, konserAd);
            ps.setString(3, tarih); ps.setString(4, konum);
            ps.setString(5, tur);   ps.setInt(6, adet);
            ps.setDouble(7, fiyat); ps.setDouble(8, toplam);
            ps.setString(9, java.time.LocalDate.now().toString());
            ps.executeUpdate();
            tfBiletAdet.setText("");

            Dialog d = new Dialog(this, "Bilet Alindi!", true);
            d.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
            d.setSize(320, 190); d.setBackground(new Color(140, 40, 90));
            Label l1 = new Label("Konser : " + konserAd);
            l1.setForeground(ALTIN); l1.setFont(FONT_LABEL);
            Label l2 = new Label("Tur    : " + tur + "  |  " + tarih);
            l2.setForeground(GRI); l2.setFont(FONT_LABEL);
            Label l3 = new Label("Adet: " + adet + "   Toplam: " + toplam + " TL");
            l3.setForeground(BEYAZ); l3.setFont(new Font("Dialog", Font.BOLD, 14));
            Button tamam = new Button("TAMAM");
            tamam.setFont(FONT_BUTON); tamam.setForeground(SIYAH);
            tamam.setPreferredSize(new Dimension(100, 30));
            tamam.addActionListener(ev -> d.dispose());
            d.add(l1); d.add(l2); d.add(l3); d.add(tamam);
            Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
            d.setLocation((ekran.width-320)/2, (ekran.height-190)/2);
            d.setVisible(true);
        } catch (Exception ex) { System.out.println("Bilet hatasi: " + ex.getMessage()); }
    }

    // -------------------------------------------------------
    //  BILET ISLEMLERI
    // -------------------------------------------------------
    private void biletleriYukle() {
        biletList.removeAll();
        double genelToplam = 0;
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT id,konser_ad,konser_tarih,konser_konum,konser_tur," +
                            "adet,birim_fiyat,toplam,satin_alma_tarihi FROM Bilet ORDER BY id DESC");
            while (rs.next()) {
                double t = rs.getDouble("toplam");
                genelToplam += t;
                biletList.add(
                        "#" + rs.getInt("id") + "|" +
                                rs.getString("konser_ad")    + "|" +
                                rs.getString("konser_tur")   + "|" +
                                rs.getString("konser_tarih") + "|" +
                                rs.getInt("adet") + " adet|" +
                                t + " TL|" +
                                rs.getString("satin_alma_tarihi"));
            }
        } catch (SQLException ex) { System.out.println("Bilet yukleme hatasi: " + ex.getMessage()); }
        lblToplamTutar.setText("Toplam Harcama: " + String.format("%.2f", genelToplam) + " TL");
    }

    private void biletIptalEt() {
        String secili = biletList.getSelectedItem();
        if (secili == null) return;
        try {
            int id = Integer.parseInt(secili.split("\\|")[0].replace("#","").trim());
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Bilet WHERE id=?");
            ps.setInt(1, id); ps.executeUpdate();
            biletleriYukle();
        } catch (Exception ex) { System.out.println("Bilet iptal hatasi: " + ex.getMessage()); }
    }

    public static void main(String[] args) { new KonserTakipSistemi(); }
}