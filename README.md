# evolutionary-tree-of-life
Java tree project for analyzing species using Tree of Life dataset.


# Evolutionary Tree of Life – CME2201 Assignment 2

This project is a **Java-based implementation of the Tree of Life (ToL)**, developed for the Data Structures course (CME2201 - Fall 2024–2025). The system models biological species as nodes in a general tree, allowing for data loading, traversal, analysis, and evolutionary insight based on real data.

---

##  Dataset

The project uses two CSV files:

- `treeoflife_nodes.csv`: Contains species data (ID, name, extinct status, confidence, phylogenetic info, etc.)
- `treeoflife_links.csv`: Contains parent-child relationships between nodes

Each species is represented as a node, and nodes are linked to form a full evolutionary tree.

---

## ⚙️ Project Structure

- `TreeNode.java`: Core class representing each node/species
- `EvolutionaryTree.java`: Main tree implementation with traversal and analysis logic
- `TreeInterface.java`: Interface defining tree methods (e.g., height, isEmpty, etc.)
- `TreeIteratorInterface.java`: Interface for tree traversal operations
- `Main.java`: Menu-based console interface for interacting with the tree

---

##  Functionalities

The system provides a menu with the following options:

1. **Load Dataset** – Reads both node and link CSV files
2. **Search Species by ID** – Displays details about a species
3. **Pre-order Traversal** – Saves all nodes in pre-order to a text file
4. **Print Subtree** – Prints the subtree of any given species in hierarchical view
5. **Print Ancestor Path** – Shows the full lineage of a species from root to leaf
6. **Find Most Recent Common Ancestor (MRCA)** – Identifies the nearest shared ancestor of two species
7. **Calculate Tree Properties** – Displays:
   - Tree height (max depth)
   - Degree (max number of children)
   - Breadth (number of leaf nodes)
8. **Print Longest Evolutionary Path** – Displays the longest path from the root to a leaf
9. **Exit**

---

##  Learning Objectives

- Implement a general tree structure in Java
- Use ADTs, OOP principles, and exception handling
- Perform pre-order traversal and parent/child navigation
- Analyze real biological data using tree algorithms
- Gain experience with file I/O and dataset integration

---

##  How to Run

1. Compile all Java files:

2. Run the main class:





3. Use the console menu to navigate the functionalities.

Make sure `treeoflife_nodes.csv` and `treeoflife_links.csv` are placed in the root directory.


---

## License

This project is for educational and academic use only.
