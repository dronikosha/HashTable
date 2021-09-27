package ru.siaod_2;

import java.io.*;

class Data implements Serializable  {
    String key;
    String author;
    String name;

    public Data(String key, String author, String name) {
        this.key = key;
        this.author = author;
        this.name = name;
    }

    public Data clone() {
        return new Data(key, author, name);

    }

    @Override
    public String toString() {
        return "ISMB = " + key + " | Author: " + author + " | Book: " + name;
    }
}

class HashTable implements Serializable  {

    private int HASH_TABLE_SIZE;
    private int size;
    private Data[] table;
    private final int primeSize;

    public HashTable(int ts) {
        
        size = 0;
        HASH_TABLE_SIZE = ts;
        table = new Data[HASH_TABLE_SIZE];

        
        for (int i = 0; i < HASH_TABLE_SIZE; i++)
            table[i] = null;
        primeSize = getPrime();
    }

    private int hash_function_1(String y) {
        int hash_functionVal1 = y.hashCode();
        hash_functionVal1 %= HASH_TABLE_SIZE;
        if (hash_functionVal1 < 0)
            hash_functionVal1 += HASH_TABLE_SIZE;
        return hash_functionVal1;
    }

    private int hash_function_2(String y) {
        int hash_functionVal2 = y.hashCode();
        hash_functionVal2 %= HASH_TABLE_SIZE;
        if (hash_functionVal2 < 0)
            hash_functionVal2 += HASH_TABLE_SIZE;
        return primeSize - hash_functionVal2 % primeSize;
    }

    public int getPrime() {
        
        for (int i = HASH_TABLE_SIZE - 1; i >= 1; i--) {
            int cnt = 0;
            for (int j = 2; j * j <= i; j++)
                if (i % j == 0)
                    cnt++;
            if (cnt == 0)
                return i;
        }
        return 3;
    }

    public void save_file() {
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream("table.txt"));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Data find_by_key(String key) {
        int hash1 = hash_function_1(key);
        int hash2 = hash_function_2(key);

        while (table[hash1] != null
                && !table[hash1].key.equals(key)) {
            hash1 += hash2;
            hash1 %= HASH_TABLE_SIZE;
        }
        return table[hash1];
    }
    
    public void insert_element(String key, String author, String name) {
        if (size >= HASH_TABLE_SIZE) {
            rehash_table();
        }
        int hashing1 = hash_function_1(key);
        int hashing2 = hash_function_2(key);
        while (table[hashing1] != null) {
            hashing1 += hashing2;
            hashing1 %= HASH_TABLE_SIZE;
        }

        table[hashing1] = new Data(key, author, name);
        size++;
        save_file();
    }
    
    public void remove_element(String key) {
        int hash1 = hash_function_1(key);
        int hash2 = hash_function_2(key);
        while (table[hash1] != null
                && !table[hash1].key.equals(key)) {
            hash1 += hash2;
            hash1 %= HASH_TABLE_SIZE;
        }
        table[hash1] = null;
        size--;
        save_file();
    }



    public void printHashTable() {
        System.out.println("\nHash Table");
        for (int i = 0; i < HASH_TABLE_SIZE; i++)
            if (table[i] != null)
                System.out.println("ISBM = " + table[i].key + " | Author: " + table[i].author + " | Book = " + table[i].name);
    }

    private void rehash_table() {
        Data[] oldTable = table.clone();
        HASH_TABLE_SIZE *= 2;
        table = new Data[HASH_TABLE_SIZE];
        for (Data data : oldTable) {
            int hashing1 = hash_function_1(data.key);
            int hashing2 = hash_function_2(data.key);
            while (table[hashing1] != null) {
                hashing1 += hashing2;
                hashing1 %= HASH_TABLE_SIZE;
            }
            table[hashing1] = data.clone();
        }
    }
}


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        HashTable ht = new HashTable(2);
        System.out.println("Hash Table was created\n");

        System.out.println("Starting adding elements\n");
        ht.insert_element("12345678901234567890", "Pushkin A.S.", "Onegin");
        ht.insert_element("09876543211234567890", "Tolstoy L.N.", "Woyna and Mir");
        ht.insert_element("09876543210987654321", "Krylov N.S.", "Lygushka and Vol");
        ht.insert_element("12312312340980980987", "Lermontov M.Y.", "Mciri");
        ht.insert_element("12345123451234512345", "Maykovski V.V.", "About It");
        System.out.println("Elements were added\n");
        ht.printHashTable();

        ht.remove_element("09876543210987654321");
        System.out.println("Element was deleted");
        ht.printHashTable();


        System.out.println("\n\nFinding the exact element");
        try {
            System.out.println(ht.find_by_key("12345123451234512345").toString() + "\n\n");
        } catch (NullPointerException e) {
            System.out.println("No such element\n\n");
        }

        System.out.println("Reading table from the file........../");
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("table.txt"));
        HashTable table = (HashTable) inputStream.readObject();
        table.printHashTable();
    }
}