
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
         EvolutionaryTree<String> tree = new EvolutionaryTree<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Load Dataset");
            System.out.println("2. Search for Species");
            System.out.println("3. Traverse Tree and Save Pre-order");
            System.out.println("4. Print the subtree of a given species in pre-order");
            System.out.println("5. Print Ancestor Path for a Node");
            System.out.println("6. Find Most Recent Common Ancestor of Two Nodes");
            System.out.println("7. Calculate Height, Degree, and Breadth of the Tree");

            System.out.println("8. Print the Longest Evolutionary Path");

            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n========== LOAD DATASET ==========");
                    String nodesFile = "treeoflife_nodes.csv";
                    String linksFile = "treeoflife_links.csv";

                    System.out.println("\nLoading dataset...");
                    tree.loadFromCSV(nodesFile, linksFile);

                    if (tree.isEmpty()) {
                        System.out.println("Error: No data loaded. Please check your files.");
                    } else {
                        System.out.println("Dataset loaded successfully.\n");
                        System.out.println("Total records loaded: " + tree.getNumberOfNodes());
                        System.out.println("Tree Root: " + tree.getRootData());
                        System.out.println("Tree Height: " + tree.getHeight());
                    }
                    System.out.println("==================================\n");
                    break;
                case 2:
                    System.out.print("Enter the species ID: ");
                    int speciesId = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    TreeNode<String> node = tree.nodeTable.get(speciesId);
                    if (node != null) {
                        System.out.println("\nOutput:");
                        System.out.println("Id: " + node.nodeId);
                        System.out.println("Name: " + node.nodeName);
                        System.out.println("Child count: " + node.children.size());
                        System.out.println("Leaf node: " + node.getLeafNodeDescription());
                        System.out.println("Link: " + (node.hasTolOrgLink ? "http://tolweb.org/" + node.nodeName.replace(" ", "_") + "/" + node.nodeId : "N/A"));
                        System.out.println("Extinct: " + node.getExtinctDescription());
                        System.out.println("Confidence: " +  node.getConfidenceDescription());
                        System.out.println("Phylesis: " + node.getPhylesisDescription());
                    } else {
                        System.out.println("Species not found!");
                    }
                    break;

                case 3:
                    System.out.println("Pre-order Traversal:");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("preorder_output.txt"))) {
                        Iterator<TreeNode<String>> iterator = tree.getPreorderIterator();
                        while (iterator.hasNext()) {
                            TreeNode<String> currentNode = iterator.next();
                            String formattedOutput = "Node ID: " + currentNode.nodeId + ", Data: " + currentNode.nodeName;
                            System.out.println(formattedOutput);
                            writer.write(formattedOutput);
                            writer.newLine();
                        }
                        System.out.println("Pre-order traversal saved to preorder_output.txt.");
                    } catch (IOException e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        System.out.print("Enter the subtree root ID: ");
                        int subtreeId = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        if (!tree.nodeTable.containsKey(subtreeId)) {
                            System.out.println("Error: Node with ID " + subtreeId + " does not exist.");
                        } else {
                            System.out.println("Subtree of node " + subtreeId + ":");
                            tree.printSubtree(subtreeId);
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid integer.");
                        scanner.nextLine(); // Consume invalid input
                    }
                    break;


                case 5:
                    System.out.print("Enter Node ID for Ancestor Path: ");
                    int ancestorId = scanner.nextInt();
                    tree.printAncestorPath(ancestorId);
                    break;


                case 6:
                    System.out.print("Enter the first Node ID: ");
                    int nodeId1 = scanner.nextInt();
                    System.out.print("Enter the second Node ID: ");
                    int nodeId2 = scanner.nextInt();

                    tree.printMostRecentCommonAncestor(nodeId1, nodeId2);
                    break;
                case 7:
                    System.out.println("Tree Properties:");
                    System.out.println("Height: " + tree.getHeight());
                    System.out.println("Degree: " + tree.getDegree());
                    System.out.println("Breadth: " + tree.getBreadth());
                    break;
                case 8:
                    System.out.println("Longest Evolutionary Path:");
                    tree.printLongestEvolutionaryPath();
                    break;


                case 0:
                    System.out.println("Exiting the program. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}