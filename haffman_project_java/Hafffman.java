import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

// Node class for Huffman Tree
class Node implements Comparable<Node> {
    char character; // Character stored in the node
    int freq; // Frequency of the character
    Node left, right; // Left and right children of the node

    // Constructor for the Node class
    public Node(char character, int freq) {
        this.character = character;
        this.freq = freq;
        this.left = null;
        this.right = null;
    }

    //Implement the compareTo method to allow Node objects to be compared based on frequency
    @Override
    public int compareTo(Node other) {
        return this.freq - other.freq; // Compare based on frequency
    }
}

// MinHeap class for managing Huffman nodes
//implement the minheap by using the array 
class MinHeap {
    private Node[] heap; //array of Node objects
    private int size;//repesent the size of the heap

    public MinHeap(int capacity) {
        heap = new Node[capacity]; //inisalize arr size by capacity
        size = 0;//inisial size of the heap
    }

    public void insert(Node node) {
        heap[size] = node; //The new node is placed at the end of the array (heap[size] = node).
        int current = size;
        //if chald freq is less then the parent freq then swap the nodes
        while (current > 0 && heap[current].freq < heap[parent(current)].freq) {
            //swapping with its parent until the heap property is restored (i.e., the parent always has a smaller frequency than the child).
            swap(current, parent(current));
            current = parent(current);
        }
        size++;
    }

    public Node extractMin() {
        Node min = heap[0];
        heap[0] = heap[--size];
        Heapify(0);
        return min;
    }

    private void Heapify(int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int smallest = i;//root index
        //yadi left ki freq kam ho root ki freq se to swap
        if (left < size && heap[left].freq < heap[smallest].freq) {
            smallest = left;
        }
        //yadi right ki freq kam ho root ki freq se to swap
        if (right < size && heap[right].freq < heap[smallest].freq) {
            smallest = right;
        }
        //If the smallest node is not the current node, it swaps them and recursively calls Heapify() on the affected subtree.
        if (smallest != i) {
            swap(i, smallest);
            Heapify(smallest);
        }
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return (2 * i) + 1;
    }

    private int rightChild(int i) {
        return (2 * i) + 2;
    }

    private void swap(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public int getSize() {
        return size;
    }
}



//---------------------------------------------------------------------------------------------------------------------------------------------------------------




/*Hafffman class: Contains the main logic for reading the input file, compressing it using Huffman coding, and writing the compressed data to an output file. It also handles decompressing the file to restore the original data. */
public class Hafffman {
    private static HashMap<Character, String> huffmanCodes = new HashMap<>(); // Map to store Huffman <char, corresponding codes>
    private static Node root;// Root node of the Huffman Tree

    public static void main(String[] args) {
        try (
            
            // Opening input file in read-only mode
            FileInputStream inputFile = new FileInputStream("sample.txt");
            //open output file in write only mode
            FileOutputStream outputFile = new FileOutputStream("sample-compressed.txt")) {

            // Example character frequencies (adjust according to your file)
            // Create frequency table
            int[] freq = new int[256]; //Frequency array for extended ASCII
            int c;
            while ((c = inputFile.read()) != -1) { //read until EOF
                freq[c]++;
            }

            // Build the Huffman Tree
            root = buildHuffmanTree(freq); // Build tree using frequency array
            generateHuffmanCodes(root, ""); // Generate Huffman codes

            // Compress the file
            compressFile("sample.txt", "sample-compressed.txt"); // Compress input file

            // Decompress the file
            decompressFile("sample-compressed.txt", "sample-decompressed.txt"); // Decompress output file

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }
    }



//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




/*Huffman Tree Construction: The buildHuffmanTree function builds the Huffman Tree using a min-heap of nodes, where each node represents a character and its frequency in the input file.*/
    // Function to build Huffman Tree
    private static Node buildHuffmanTree(int[] freq) {
        MinHeap minHeap = new MinHeap(256);
        for (char i = 0; i < freq.length; i++) {//frq arr ki length = 256
            if (freq[i] > 0) { //yadi char ki freq 1 ya 1 se jayada h 
                minHeap.insert(new Node(i, freq[i]));//mainheap(char as a integer form , freq of that char)
            }
        }
        // Repeat until the heap contains only one node (root of the Huffman Tree)
        while (minHeap.getSize() > 1) {
            // Extract the two nodes with the smallest frequencies
            Node left = minHeap.extractMin();
            Node right = minHeap.extractMin();
        // Create a new internal node with a frequency equal to the sum of the two nodes' frequencies
        // '$' is used as a dummy character for internal nodes
            Node parent = new Node('$', left.freq + right.freq);
        // Set the two extracted nodes as the left and right children of the new node
        //less freq char made as a left child and more freq char made as a right child 
            parent.left = left;// Assign left child
            parent.right = right;// Assign right child
            // Insert this new node into the Min Heap
            minHeap.insert(parent);
        }
         // The remaining node in the Min Heap is the root of the Huffman Tree
        return minHeap.extractMin();
    }




//--------------------------------------------------------------------------------------------------------------------------------------------------------------

     // Generate Huffman Codes
    private static void generateHuffmanCodes(Node node, String code) {
        if (node.left == null && node.right == null) { // Leaf node
            huffmanCodes.put(node.character, code); // Map<character,  char corresponding unique code> genratred by haffman tree
            return;
        }
        // Traverse the tree
        generateHuffmanCodes(node.left, code + '0'); // Append '0' for left edges
        generateHuffmanCodes(node.right, code + '1'); // Append '1' for right edges
    }





//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





// Function to compress file
/*File Compression: The file is read byte by byte, and each byte is replaced with its Huffman code (a sequence of bits) and written to the output file in a compressed format. */
    private static void compressFile(String inputFilePath, String outputFilePath) throws IOException {
        FileInputStream fis = new FileInputStream(inputFilePath);
        FileOutputStream fos = new FileOutputStream(outputFilePath);

        StringBuilder binaryString = new StringBuilder(); // StringBuilder to hold the binary string
        int c;
         // Codes are written into the file in bit-by-bit format
        while ((c = fis.read()) != -1) {// Read until EOF
            binaryString.append(huffmanCodes.get((char) c)); // har ek char ke corresponding jo bhi particular bit code h use binarystring me append
        }

        // Write bits to file
        int byteSize = 0; // Track the size of the current byte
        int buffer = 0; // Buffer to hold bits
        for (int i = 0; i < binaryString.length(); i++) {
            buffer = (buffer << 1) | (binaryString.charAt(i) - '0'); // Shift buffer and add the bit
            byteSize++;
            if (byteSize == 8) { // If the buffer is full (8 bits) // 1 byte = 8 bits
                fos.write(buffer); // Write the byte to the output file
                buffer = 0; // Reset the buffer
                byteSize = 0; // Reset the byte size
            }
        }
        
        // Write remaining bits
        if (byteSize > 0) {
            buffer <<= (8 - byteSize); // Shift remaining bits to fill the byte
            fos.write(buffer); // Write the last byte
        }
        
        fis.close();
        fos.close();
    }




//---------------------------------------------------------------------------------------------------------------------------------------------------------




    // Decompress the file
    private static void decompressFile(String inputFilePath, String outputFilePath) throws IOException {
        FileInputStream fis = new FileInputStream(inputFilePath);
        FileOutputStream fos = new FileOutputStream(outputFilePath);

        StringBuilder binaryString = new StringBuilder(); // StringBuilder to hold binary representation
        int c;
        while ((c = fis.read()) != -1) {
            for (int i = 7; i >= 0; i--) { // Read each byte and convert to binary insure that read the bytes in to the reverse order
                binaryString.append((c >> i) & 1); // Append each bit to the binary string
            }
        }

        Node current = root; // Start from the root of the Huffman tree
        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '0') {
                current = current.left; // Traverse left for '0'
            } else {
                current = current.right; // Traverse right for '1'
            }
            // If a leaf node is reached, write the character to the output file
            if (current.left == null && current.right == null) {
                fos.write(current.character); // Write character to output file
                current = root; // Reset to root for next character
            }
        }

        fis.close();
        fos.close();
    }
}
