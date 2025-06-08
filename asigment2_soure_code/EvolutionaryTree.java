import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.*;
import java.util.*;
import java.util.LinkedList;


public class EvolutionaryTree<T> implements TreeInterface<T>, TreeIteratorInterface<T> {
    public Hashtable<Integer, TreeNode<T>> nodeTable = new Hashtable<>(); // Tüm düğümleri saklamak için bir tablo
    public TreeNode<T> root; // Kök düğüm referansı

    //kök verisini döndören method
    @Override
    public T getRootData() {
        if (root != null) {
            return root.nodeName;
        } else {
            return null;
        }
    }


    // Kök düğümü döndüren bir metod
    public TreeNode<T> getRootNode() {
        // Eğer kök düğüm null ise, ağacın başlatılmadığını belirtmek için bir hata fırlatılır.
        if (root == null) {
            throw new IllegalStateException("Root is not initialized!"); // Hata mesajı
        }
        // Eğer kök düğüm mevcutsa, kök düğüm nesnesini döndürür.
        return root;
    }

    // Ağacın toplam düğüm sayısını döndüren bir metod
    @Override
    public int getNumberOfNodes() {
        // nodeTable, tüm düğümleri saklayan bir Hashtable'dır.
        // Hashtable'daki toplam düğüm sayısını döndürür.
        return nodeTable.size();
    }


/**Bu metod, nodesFile (düğüm verileri) ve linksFile (bağlantı verileri) dosyalarını okuyarak ağacı oluşturur.
  Her iki dosyadaki satırları işlemek için processNodeLine ve processLinkLine metotlarını kullanır.
   Hatalı satırlar bir hata günlüğüne kaydedilir. Ayrıca kök düğüm bulunarak ağaca atanır.

 */

    // CSV dosyalarından düğüm ve bağlantı verilerini yükleyen metod
    public void loadFromCSV(String nodesFile, String linksFile) {
        // Try-with-resources: Kaynakları (dosya okuyucular ve yazıcı) otomatik kapatmak için kullanılır
        try (BufferedReader nodeReader = new BufferedReader(new FileReader(nodesFile)); // Düğüm dosyasını okuyucu
             BufferedReader linkReader = new BufferedReader(new FileReader(linksFile)); // Bağlantı dosyasını okuyucu
             BufferedWriter errorLogWriter = new BufferedWriter(new FileWriter("error_log.txt", true))) { // Hata günlüğü yazıcı

            //  Düğüm dosyasını okuma
            nodeReader.readLine(); // Başlık satırını atla
            String line;
            while ((line = nodeReader.readLine()) != null) { // Satır satır oku
                try {
                    processNodeLine(line); // Satırı işleyen metodu çağır
                } catch (Exception e) {
                    logError(errorLogWriter, line, e); // Hata durumunda satırı hata günlüğüne yaz
                }
            }

            //  Bağlantı dosyasını okuma
            linkReader.readLine(); // Başlık satırını atla
            while ((line = linkReader.readLine()) != null) { // Satır satır oku
                try {
                    processLinkLine(line); // Satırı işleyen metodu çağır
                } catch (Exception e) {
                    logError(errorLogWriter, line, e); // Hata durumunda satırı hata günlüğüne yaz
                }
            }

            //  Kök düğümü bulma
            int rootNodeId = findRootId(); // Kök düğüm ID'sini bul
            root = nodeTable.get(rootNodeId); // Kök düğümü nodeTable'dan al

            //  Kök düğümün başarıyla ayarlanıp ayarlanmadığını kontrol et
            if (isEmpty()) {
                System.out.println("Error: Could not set root node!"); // Eğer kök düğüm ayarlanmadıysa uyarı ver
            }

        } catch (IOException e) {
            // Dosyaların okunmasında oluşan hataları yakala
            System.out.println("Dosyaları okurken hata oluştu: " + e.getMessage());
        }
    }


    /**getHeight: Ağacın yüksekliğini döndürür.
     * calculateHeight: Verilen bir düğümden başlayarak o düğümün yüksekliğini hesaplar.
     Rekürsif olarak alt düğümleri dolaşır ve en uzun yolun yüksekliğini bulur.
    */

    @Override
    public int getHeight() {
        // Eğer ağaç boşsa, yüksekliği 0 olarak döndür
        if (isEmpty()) {
            return 0;
        }
        // Eğer ağaç boş değilse, kök düğümden başlayarak yüksekliği hesapla
        return calculateHeight(getRootNode());
    }

    /**
      Belirtilen düğümün yüksekliğini hesaplar.
      @param node İşlem yapılacak düğüm.
      @return Düğümün yüksekliği (alt ağaçlarla birlikte).*/


    private int calculateHeight(TreeNode<T> node) {
        // Eğer düğüm null ise (boş düğüm), yüksekliği 0 olarak döndür
        if (node == null) {
            return 0;
        }

        int height = 0; // Yüksekliği tutmak için bir değişken
        // Düğümün tüm çocuklarını dolaş
        for (int i = 0; i < node.children.size(); i++) {
            TreeNode<T> child = node.children.get(i); // Çocuk düğümü al
            int childHeight = calculateHeight(child); // Çocuğun yüksekliğini hesapla
            if (childHeight > height) { // Mevcut yüksekliği güncelle
                height = childHeight;
            }
        }

        // Mevcut düğümün yüksekliği, en uzun alt ağacın yüksekliği + 1 (bu düğüm)
        return height + 1;
    }


    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        nodeTable.clear();
        root = null;
    }


     public int getBreadth() {
        TreeNode<T> rootNode = getRootNode();
        if (rootNode == null) return 0; // Eğer kök yoksa, genişlik 0'dır.

        int leafCount = 0; // Yaprak düğüm sayısını tutar.
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.add(rootNode); // Kök düğümü kuyruğa ekle.

        while (!queue.isEmpty()) {
            TreeNode<T> current = queue.poll(); // Kuyruktan bir düğüm çıkar.

            // Eğer düğümün çocuğu yoksa yaprak düğümdür.
            if (current.children.isEmpty()) {
                leafCount++;
            } else {
                // Çocuğu olan düğümün çocuklarını kuyruğa ekle.
                queue.addAll(current.children);
            }
        }

        return leafCount; // Tüm yaprak düğümleri sayıldıktan sonra sonucu döndür.
    }





    //*****************************************************

    @Override
    public Iterator<TreeNode<T>> getPreorderIterator() {
        return new Iterator<TreeNode<T>>() {
            // Bu iterator, pre-order dolaşımı gerçekleştirmek için kullanılır.
            private final Stack<TreeNode<T>> stack = new Stack<>(); // Düğümleri işlemek için bir yığın kullanılır.

            // Yığın başlangıçta kök düğümle başlatılır.
            {
                if (root != null) {
                    stack.push(root); // Eğer kök düğüm varsa, yığına eklenir.
                }
            }

            @Override
            public boolean hasNext() {
                // Yığın boş değilse daha fazla düğüm vardır.
                return !stack.isEmpty();
            }

            @Override
            public TreeNode<T> next() {
                // Eğer yığında düğüm kalmadıysa, bir hata fırlatılır.
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                // Yığından bir düğüm alınır.
                TreeNode<T> currentNode = stack.pop();
                // Çocuk düğümler ters sırada yığına eklenir. Böylece, doğru sırayla işlenir.
                LinkedList<TreeNode<T>> childrenReversed = new LinkedList<>(currentNode.children);
                Collections.reverse(childrenReversed);
                stack.addAll(childrenReversed);
                return currentNode; // Mevcut düğüm döndürülür.
            }
        };
    }







        /**Bu metot, bir CSV dosyasından alınan bir satırı işler
          ve bu satırdaki bilgileri bir TreeNode nesnesine dönüştürerek,
         düğümü bir Hashtable içinde saklar.*/

    public void processNodeLine(String line) {
        // Satırı virgül ile ayırarak bir diziye dönüştür
        // Her sütun, dizi elemanı olarak saklanır
        String[] parts = line.split(",");

        // Eğer parça sayısı 8'den azsa, eksik kolonlar olduğunu belirten bir hata fırlat
        if (parts.length < 8) {
            throw new IllegalArgumentException("Missing columns: " + Arrays.toString(parts));
        }

        // İlk sütunu (ID) bir tamsayıya dönüştür ve ID olarak sakla
        int id = Integer.parseInt(parts[0].trim());

        // İkinci sütunu düğüm adı (T tipi) olarak sakla
        T name = (T) parts[1].trim();

        // Üçüncü sütunu yaprak düğüm durumu olarak değerlendir (true/false)
        // "1" yaprak düğüm olduğunu ifade eder
        boolean isLeaf = parts[3].trim().equals("1");


        // Dördüncü sütunu düğümün tolweb.org'da bir sayfası olup olmadığını belirlemek için kullan
        // "1" var, "0" yok anlamına gelir
        boolean hasTolOrgLink = parts[4].trim().equals("1");

        // Beşinci sütunu türün soyu tükenip tükenmediğini belirtmek için kullan
        // "1" soyu tükenmiş, "0" yaşayan anlamına gelir
        boolean isExtinct = parts[5].trim().equals("1");

        // Altıncı sütunu bir tamsayıya dönüştür ve düğümün güven seviyesi olarak sakla
        // Örn   0 (güvenilir), 1 (şüpheli), 2 (belirsiz)
        int confidence = Integer.parseInt(parts[6].trim());

        // Yedinci sütunu bir tamsayıya dönüştür ve düğümün filogenetik statüsü olarak sakla
        // Örn      0 (monofiletik), 1 (şüpheli monofili), 2 (monofiletik değil)
        int phylesis = Integer.parseInt(parts[7].trim());

        // Yeni bir TreeNode nesnesi oluştur ve yukarıdaki değerleri parametre olarak kullan
        TreeNode<T> node = new TreeNode<>(id, name, 0, isLeaf, hasTolOrgLink, isExtinct, confidence, phylesis);

        // Oluşturulan düğümü nodeTable'a (Hashtable) ekle
        // Anahtar: Düğüm ID'si, Değer: TreeNode nesnesi
        nodeTable.put(id, node);
    }




        /**Bu metot, bir CSV dosyasından alınan bir satırı işler ve
          bu satırdaki parent-child ilişkisini TreeNode nesneleri arasında kurar.*/

    public void processLinkLine(String line) {
        // Satırı virgül ile ayırarak bir diziye dönüştür
        // Her sütun, dizi elemanı olarak saklanır
        String[] parts = line.split(",");

        // Eğer parça sayısı 2'den azsa, eksik kolonlar olduğunu belirten bir hata fırlat
        if (parts.length < 2) {
            throw new IllegalArgumentException("Missing columns: " + Arrays.toString(parts));
        }

        // İlk sütunu (Parent ID) bir tamsayıya dönüştür ve parentId olarak sakla
        int parentId = Integer.parseInt(parts[0].trim());

        // İkinci sütunu (Child ID) bir tamsayıya dönüştür ve childId olarak sakla
        int childId = Integer.parseInt(parts[1].trim());

        // `nodeTable` içinde Parent ID'ye karşılık gelen düğümü al
        TreeNode<T> parent = nodeTable.get(parentId);

        // `nodeTable` içinde Child ID'ye karşılık gelen düğümü al
        TreeNode<T> child = nodeTable.get(childId);

        // Hem parent hem de child düğümleri bulunmuşsa işlemleri gerçekleştir
        if (parent != null && child != null) {
            // Parent düğümüne child düğümünü ekle
            parent.addChild(child);

            // Başarıyla ekleme işlemini konsola yazdır
            System.out.println("Parent ID: " + parent.nodeId + " added Child ID: " + child.nodeId);
        } else {
            // Eğer parent veya child düğümü eksikse, durumu konsola yazdır
            System.out.println("Missing Parent or Child: Parent ID: " + parentId + ", Child ID: " + childId);
        }
    }







    private void logError(BufferedWriter writer, String line, Exception e) {
        try {
            writer.write("eror : " + line + " - " + e.getMessage());
            writer.newLine();
        } catch (IOException ex) {
            System.out.println("Failed to write to error log file: " + ex.getMessage());    //hata log dosyasına yazdirilamadi
        }
    }


    public int findRootId() {
        // Her düğümün referans sayısını tutan bir Hashtable
        // Anahtar: Düğüm ID'si, Değer: Bu düğüme yapılan referans sayısı
        Hashtable<Integer, Integer> referenceCount = new Hashtable<>();

        // Tüm düğümlerin başlangıçta 0 referansa sahip olduğunu varsayıyoruz
        // nodeTable içindeki tüm düğümleri dolaşarak başlangıç değerlerini ayarla
        Iterator<TreeNode<T>> nodeIterator = nodeTable.values().iterator();
        while (nodeIterator.hasNext()) {
            TreeNode<T> node = nodeIterator.next();
            referenceCount.put(node.nodeId, 0); // Her düğümün başlangıç referans sayısı 0
        }

        // Çocuk düğümleri dolaşıp referans sayılarını artırıyoruz
        nodeIterator = nodeTable.values().iterator();
        while (nodeIterator.hasNext()) {
            TreeNode<T> node = nodeIterator.next();
            for (TreeNode<T> child : node.children) {
                // Çocuk düğümün mevcut referans sayısını al ve bir artır
                referenceCount.put(child.nodeId, referenceCount.get(child.nodeId) + 1);
            }
        }

        // Referans sayısı 0 olan düğümü buluyoruz
        // Bu düğüm kök düğümdür çünkü hiçbir düğüm onu çocuk olarak göstermiyor
        Iterator<Integer> keyIterator = referenceCount.keySet().iterator();
        while (keyIterator.hasNext()) {
            int key = keyIterator.next();
            if (referenceCount.get(key) == 0) { // Referans edilmemiş düğümü kontrol et
                return key; // Kök düğüm ID'sini döndür
            }
        }

        // Eğer referans sayısı 0 olan hiçbir düğüm bulunamazsa hata fırlatılır
        throw new IllegalStateException("Root node not found.");
    }



  /**  Genişlik Öncelikli Dolaşım (Breadth-First Traversal):
    Kuyruk kullanılarak her seviyedeki düğümler sırasıyla işlenir.
    Maksimum Dereceyi Bulma:
    Her düğümün çocuk sayısı kontrol edilir ve bu değer maksimum dereceyle karşılaştırılarak güncellenir.*/

    public int getDegree() {
        // Eğer ağaç boşsa, maksimum derece 0'dır
        if (isEmpty()) return 0;

        int maxDegree = 0; // Ağaçtaki maksimum düğüm derecesini saklayan değişken

        // Genişlik öncelikli dolaşım için bir kuyruk oluşturuyoruz
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.add(root); // Kuyruğa kök düğümü ekliyoruz

        // Kuyruk boş olana kadar döngü devam eder
        while (!queue.isEmpty()) {
            TreeNode<T> current = queue.poll(); // Kuyruğun başındaki düğümü al

            // Mevcut düğümün çocuk sayısını kontrol et
            int currentDegree = current.children.size();
            // Maksimum dereceyi güncelle
            maxDegree = Math.max(maxDegree, currentDegree);

            // Mevcut düğümün çocuklarını kuyruğa ekle
            queue.addAll(current.children);
        }

        // Ağaçtaki en yüksek dereceli düğümün derecesini döndür
        return maxDegree;
    }






    /**
     4.4
      bir ağacın belirli bir alt ağacını (subtree) hiyerarşik bir şekilde yazdırmak için kullanılır.
     printSubtree ana metot, belirli bir düğümden başlayarak o düğüm ve tüm alt düğümlerini yazdırır.
      printSubtreeRecursive ise bu işlemi rekürsif olarak gerçekleştirir.   */


    public void printSubtree(int subtreeId) {
        // Belirtilen ID'ye sahip düğümü nodeTable'dan alıyoruz
        TreeNode<T> node = nodeTable.get(subtreeId);

        // Eğer düğüm bulunamazsa bir hata mesajı yazdırıyoruz ve metodu sonlandırıyoruz
        if (node == null) {
            System.out.println("Node not found.");
            return;
        }

        // Alt ağacın kök düğümünün ID'sini yazdırıyoruz
        System.out.println("Subtree of Node ID: " + subtreeId);

        // Rekürsif olarak alt ağacı yazdırma işlemini başlatıyoruz
        printSubtreeRecursive(node, 0);
    }


    private void printSubtreeRecursive(TreeNode<T> node, int level) {
        // Eğer düğüm null ise işlem sonlandırılır
        if (node == null) return;

        // Seviyeye bağlı olarak girinti oluşturuyoruz
        // Her seviye için '-' karakteri eklenir, bu da alt düğümlerin görsel olarak iç içe geçmiş yapısını gösterir
        String indent = "";
        for (int i = 0; i < level * 3; i++) {
            indent += "-";
        }

        // Extinct durumuna göre sembol belirleme
        // Eğer extinct == 0 ise tür yaşıyor demektir ve "+" atanır
        // Eğer extinct == 1 ise tür soyu tükenmiş demektir ve "-" atanır
        String statusSymbol;
        if (node.isExtinct) {
            statusSymbol = "(-)"; // Soyu tükenmiş tür
        } else {
            statusSymbol = "(+)"; // Yaşayan tür
        }

        // Düğüm adını kontrol ediyoruz
        // Eğer düğümün adı null değilse ve boşluklardan oluşmuyorsa, adı kullanılır
        // Aksi halde "Unnamed" atanır
        String nodeName;
        if (node.nodeName != null && !node.nodeName.toString().trim().isEmpty()) {
            nodeName = node.nodeName.toString().trim();
        } else {
            nodeName = "Unnamed";
        }

        // Düğümün bilgilerini birleştirerek yazdırıyoruz
        // Çıktı: girinti + düğüm ID'si + düğüm adı + yaprak durumu sembolü
        String output = indent + node.nodeId + "-" + nodeName + " " + statusSymbol;
        System.out.println(output);

        // Çocuk düğümleri rekürsif olarak işleriz
        // Eğer çocuk düğümler mevcutsa, her bir çocuk için aynı işlemi tekrarlıyoruz
        if (node.children != null) {
            for (TreeNode<T> child : node.children) {
                printSubtreeRecursive(child, level + 1); // Çocuk düğümü bir seviye daha içeri taşır
            }
        }
    }




    //4.5
    private TreeNode<T> findNodeById(int nodeId) {
        // nodeTable kullanarak verilen ID'ye sahip düğümü bul ve döndür
        // nodeTable, tüm düğümlerin nodeId'lerine göre hızlı erişim sağlamak için kullanılan bir Map olarak varsayılmıştır.
        return nodeTable.get(nodeId);
    }

    public void printAncestorPath(int nodeId) {
        // Hedef düğümü bulma
        TreeNode<T> targetNode = findNodeById(nodeId);
        if (targetNode == null) {
            // Eğer düğüm bulunamazsa bir hata mesajı yazdır ve işlemi sonlandır
            System.out.println("Node ID " + nodeId + " not found.");
            return;
        }

        // Atasal yolun saklanması için LinkedList kullanıyoruz
        // LinkedList, elemanları başa eklemede daha verimli olduğu için tercih edilmiştir
        LinkedList<TreeNode<T>> ancestorPath = new LinkedList<>();
        TreeNode<T> current = targetNode;

        // Ataları liste başına ekleme
        // current düğümünden başlayarak kök düğüme kadar tüm ataları listeye ekliyoruz
        while (current != null) {
            ancestorPath.addFirst(current); // Liste başına ekleme, böylece ters çevirme işlemine gerek kalmaz
            current = current.parent; // Bir üst seviyedeki ata düğüme geç
        }

        // Atasal yolun yazdırılması
        System.out.println("Ancestor Path for Node ID " + nodeId + ":");
        int level = 0; // Her düğümün ağacın seviyesindeki derinliğini takip eder
        for (TreeNode<T> node : ancestorPath) {
            // Girintiyi oluşturmak için gerekli '-' karakterlerini ekliyoruz
            // Her seviye için 2 karakterlik girinti
            String prefix = "";
            for (int j = 0; j < level * 2; j++) {
                prefix += "-";
            }

            // Extinct durumuna göre sembol belirleme
            // Eğer extinct == 0 ise tür yaşıyor demektir ve "+" atanır
            // Eğer extinct == 1 ise tür soyu tükenmiş demektir ve "-" atanır
            String statusSymbol;
            if (node.isExtinct) {
                statusSymbol = "(-)"; // Soyu tükenmiş tür
            } else {
                statusSymbol = "(+)"; // Yaşayan tür
            }

            // Düğüm bilgilerini birleştirerek yazdırıyoruz
            // Çıktı: girinti + düğüm ID'si + düğüm adı + yaprak durumu sembolü
            String output = prefix + node.nodeId + "-";
            if (node.nodeName != null) {
                output += node.nodeName; // Eğer düğüm adı mevcutsa ekle
            } else {
                output += "Unnamed"; // Eğer düğüm adı yoksa "Unnamed" olarak adlandır
            }
            output += " " + statusSymbol;
            System.out.println(output); // Hazırlanan çıktıyı yazdır

            level++; // Bir sonraki düğüm için seviyeyi artır
        }
    }









//4.6


    public List<TreeNode<T>> getAncestorPath(int nodeId) {
        // Verilen düğüm kimliği için atasal yolu bulur
        TreeNode<T> targetNode = findNodeById(nodeId); // Düğümü bul
        if (targetNode == null) {
            return null; // Eğer düğüm bulunamazsa null döndür
        }

        List<TreeNode<T>> ancestorPath = new LinkedList<>(); // Ataları saklamak için bir LinkedList oluştur
        TreeNode<T> current = targetNode;

        // Kök düğüme kadar ata düğümleri topla
        while (current != null) {
            ancestorPath.add(0, current); // Ataları liste başına ekle
            current = current.parent; // Bir üst seviyedeki ata düğüme geç
        }

        return ancestorPath; // Atasal yolu döndür
    }





    public TreeNode<T> findMostRecentCommonAncestor(int nodeId1, int nodeId2) {
        // İki düğümün atasal yollarını bul
        List<TreeNode<T>> path1 = getAncestorPath(nodeId1);
        List<TreeNode<T>> path2 = getAncestorPath(nodeId2);

        // Eğer herhangi bir düğüm bulunamazsa null döndür
        if (path1 == null || path2 == null) {
            return null;
        }

        TreeNode<T> commonAncestor = null; // Ortak atayı saklamak için bir değişken oluştur
        int index1 = 0; // İlk yolun indeksini takip et
        int index2 = 0; // İkinci yolun indeksini takip et

        // İki yol üzerinde aynı seviyedeki düğümleri karşılaştır
        while (index1 < path1.size() && index2 < path2.size()) {
            if (path1.get(index1).nodeId == path2.get(index2).nodeId) {
                commonAncestor = path1.get(index1); // Ortak düğümü kaydet
            } else {
                break; // Ortaklık sona erdiğinde döngüden çık
            }
            index1++; // İlk yolun bir sonraki düğümüne geç
            index2++; // İkinci yolun bir sonraki düğümüne geç
        }

        return commonAncestor; // En yakın ortak atayı döndür
    }

    public void printMostRecentCommonAncestor(int nodeId1, int nodeId2) {
        // En yakın ortak atayı bul
        // Bu, verilen iki düğümün atasal yollarını karşılaştırarak en yakın ortak atayı belirler
        TreeNode<T> commonAncestor = findMostRecentCommonAncestor(nodeId1, nodeId2);

        // Eğer ortak ata bulunamazsa uygun bir mesaj yazdır ve metodu sonlandır
        if (commonAncestor == null) {
            System.out.println("No common ancestor found for Node IDs: " + nodeId1 + " and " + nodeId2);
            return; // İşlem sonlandırılır çünkü ortak ata yoktur
        }

        // İlk düğümün adı belirlenir
        // Eğer düğüm bulunamazsa veya adı yoksa "Unknown" atanır
        TreeNode<T> node1 = findNodeById(nodeId1);
        String node1Name = "Unknown";
        if (node1 != null && node1.nodeName != null) {
            node1Name = (String)node1.nodeName;
        }

        // İkinci düğümün adı belirlenir
        // Eğer düğüm bulunamazsa veya adı yoksa "Unknown" atanır
        TreeNode<T> node2 = findNodeById(nodeId2);
        String node2Name = "Unknown";
        if (node2 != null && node2.nodeName != null) {
            node2Name =(String) node2.nodeName;
        }

        // Ortak ata adı belirlenir
        // Eğer ortak atanın adı yoksa "Unnamed" atanır
        String commonAncestorName = "Unnamed";
        if (commonAncestor.nodeName != null) {
            commonAncestorName = (String)commonAncestor.nodeName;
        }

        // Ortak atayı anlamlı bir şekilde yazdır
        // Çıktı, kullanıcıya hangi iki düğümün en yakın ortak ataya sahip olduğunu gösterir
        String output = "The most recent common ancestor of " + nodeId1 + "-" + node1Name +
                " and " + nodeId2 + "-" + node2Name + " is " +
                commonAncestor.nodeId + "-" + commonAncestorName + ".";
        System.out.println(output); // Sonuç ekrana yazdırılır
    }





//4.8


    public void printLongestEvolutionaryPath() {
        // Eğer ağaç boşsa, kullanıcıya bunu bildir ve işlemi sonlandır
        if (root == null) {
            System.out.println("The tree is empty.");
            return;
        }

        // En uzun yol ve mevcut yolu saklamak için LinkedList kullanıyoruz
        LinkedList<TreeNode<T>> longestPath = new LinkedList<>();
        LinkedList<TreeNode<T>> currentPath = new LinkedList<>();

        // Kök düğümden başlayarak en uzun yolu bulmak için findLongestPath metodunu çağır
        findLongestPath(root, currentPath, longestPath);

        // En uzun yolu kullanıcıya yazdır
        System.out.println("Longest Evolutionary Path:");
        int i = 0;
        while (i < longestPath.size()) {
            TreeNode<T> node = longestPath.get(i); // En uzun yoldaki mevcut düğümü al
            String prefix = "";

            // Hiyerarşiyi göstermek için gerekli '-' karakterlerini ekle
            int j = 0;
            while (j < i * 2) {
                prefix += "-";
                j++;
            }

            // Düğüm bilgilerini ekrana yazdır
            System.out.println(prefix + node.nodeId + "-" + node.nodeName);
            i++;
        }
    }

    private void findLongestPath(TreeNode<T> node, LinkedList<TreeNode<T>> currentPath, LinkedList<TreeNode<T>> longestPath) {
        // Eğer düğüm null ise, işlem sonlandırılır
        if (node == null) return;

        // Mevcut düğümü currentPath'e ekle
        currentPath.add(node);

        // Eğer düğüm bir yaprak düğümse (çocukları yoksa)
        if (node.children.isEmpty()) {
            // Mevcut yolun uzunluğu en uzun yoldan uzunsa longestPath'i güncelle
            if (currentPath.size() > longestPath.size()) {
                longestPath.clear(); // En uzun yolu temizle
                int k = 0;
                while (k < currentPath.size()) {
                    longestPath.add(currentPath.get(k)); // Mevcut yoldaki düğümleri longestPath'e ekle
                    k++;
                }
            }
        } else {
            // Eğer düğümün çocukları varsa, her bir çocuk için findLongestPath metodunu çağır
            int m = 0;
            while (m < node.children.size()) {
                findLongestPath(node.children.get(m), currentPath, longestPath);
                m++;
            }
        }

        // Rekürsif çağrıdan geri dönerken mevcut düğümü currentPath'ten çıkar
        currentPath.removeLast();
    }












}


