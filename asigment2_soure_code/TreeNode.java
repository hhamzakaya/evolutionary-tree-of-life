
import java.util.*;

class TreeNode<T> {
    int nodeId; // Düğümün benzersiz kimliği
    T nodeName; // Türün adı
    int childCount; // Çocuk düğüm sayısı
    boolean isLeaf; // Yaprak düğüm olup olmadığını belirtir
    boolean hasTolOrgLink; // Türün Tree of Life web sitesinde bir bağlantısı var mı?
    boolean isExtinct; // Türün soyu tükenmiş mi?
    int confidence; // Türün ağdaki pozisyonunun güven derecesi
    int phylesis; // Türün filogenetik durumu (monofiletik vs.)

    TreeNode<T> parent; // Ata düğüm referansı
    LinkedList<TreeNode<T>> children; // Çocuk düğümleri saklayan yapı

    // Yapıcı metod (Constructor)
    public TreeNode(int nodeId, T nodeName, int childCount, boolean isLeaf, boolean hasTolOrgLink, boolean isExtinct, int confidence, int phylesis) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.childCount = childCount;
        this.isLeaf = isLeaf;
        this.hasTolOrgLink = hasTolOrgLink;
        this.isExtinct = isExtinct;
        this.confidence = confidence;
        this.phylesis = phylesis;
        this.children = new LinkedList<>();
    }
    // Getter method for leaf node description
    public String getLeafNodeDescription() {
        if (this.isLeaf) {
            return "yes";
        } else {
            return "no";
        }
    }

    // Getter method for confidence description
    public String getConfidenceDescription() {
        switch (this.confidence) {
            case 0: return "confident position ";
            case 1: return "problematic position ";
            case 2: return "unspecified position ";
            default: return "unknown position ";
        }
    }

    // Getter method for phylesis description
    public String getPhylesisDescription() {
        switch (this.phylesis) {
            case 0: return "monophyletic";
            case 1: return "uncertain monophyly";
            case 2: return "not monophyletic";
            default: return "unknown";
        }
    }
    // Getter method for extinct description
    public String getExtinctDescription() {
        if (this.isExtinct) {
            return "extinct  species ";
        } else {
            return "living";
        }
    }


    public void addChild(TreeNode<T> child) {
        if (child == null) {
            System.out.println("Error: Attempting to add a null child to Node ID: " + this.nodeId);
            return;
        }

        child.parent = this; // Çocuğun parent referansını güncelle
        if (!this.children.contains(child)) {   //Çocuk düğümün zaten eklenip eklenmediğini kontrol eder.
                                                  //  this.children → Mevcut düğümün (parent) çocuklarının bulunduğu liste.
                                                  //.contains(child) → Eğer liste child düğümünü içeriyorsa, true döner.
            this.children.add(child);
           // System.out.println("Child Node ID: " + child.nodeId + " added to Parent Node ID: " + this.nodeId);

            // Node ilişkilerini kontrol et

           System.out.println("Node ID: " + this.nodeId + ", Name: " + this.nodeName + ", Children Count: " + this.children.size());
        }
    }




}
