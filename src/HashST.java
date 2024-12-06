/*
References: Princeton Library and slides
Wade Hunter, Julia Bonsack

 */

import java.io.*;

public class HashST<Key, Value> {
    private static final int INIT_CAPACITY = 4;
    private int n;
    private int m;
    private Node[] st;
    private int searchCost = 0;


    private static class Node {
        private final Object key;
        private Object val;
        private Node next;

        public Node(Object key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    public HashST() {
        this(INIT_CAPACITY);
    }

    public HashST(int m) {
        this.m = m;
        st = new Node[m];
    }

    @SuppressWarnings("unchecked")
    private void resize(int chains) {
        HashST<Key, Value> temp = new HashST<>(chains);
        for (int i = 0; i < m; i++) {
            for (Node x = st[i]; x != null; x = x.next) {
                temp.put((Key) x.key, (Value) x.val);
            }
        }
        this.m = temp.m;
        this.n = temp.n;
        this.st = temp.st;
    }

    private int hash(Key key) {
        int hash = 0;
        String strKey = (String) key; // Assumes keys are strings
        for (int i = 0; i < strKey.length(); i++) {
            hash = (hash * 31) + strKey.charAt(i);
        }
        return (hash & 0x7fffffff) % m;
    }

    public int getSearchCost() {
        return searchCost;
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }


    @SuppressWarnings("unchecked")
    public Value get(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        searchCost = 0;
        int i = hash(key);
        for (Node x = st[i]; x != null; x = x.next) {
            searchCost++;
            if (key.equals(x.key)) return (Value) x.val;
        }
        return null;
    }

    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) {
            remove(key);
            return;
        }

        if (n >= 10 * m) resize(2 * m);

        int i = hash(key);
        for (Node x = st[i]; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        n++;
        st[i] = new Node(key, val, st[i]);
    }

    public void remove(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to remove() is null");

        int i = hash(key);
        st[i] = remove(st[i], key);

        if (m > INIT_CAPACITY && n <= 2 * m) resize(m / 2);
    }

    private Node remove(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            n--;
            return x.next;
        }
        x.next = remove(x.next, key);
        return x;
    }

    public Iterable<Key> keys() {
        Queue<Key> queue = new Queue<>(); // this queue class is from princton textbook
        for (int i = 0; i < m; i++) {
            for (Node x = st[i]; x != null; x = x.next) {
                queue.enqueue((Key) x.key); // enque cuz add wasnt working
            }
        }
        return queue;
    }

    public static HashST<String, Integer> loadDictionary(String filename, int m) throws IOException {
        HashST<String, Integer> dictionary = new HashST<>(m);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String word;
        int line = 1;
        while ((word = br.readLine()) != null) {
            dictionary.put(word.trim(), line++);
        }
        br.close();
        return dictionary;
    }

    public static boolean isStrongPassword(String password, HashST<String, Integer> dictionary) {
        if (password.length() < 8) return false;

        if (dictionary.contains(password)) return false;

        for (int i = 0; i < 10; i++) {
            if (dictionary.contains(password.substring(0, password.length() - 1))) return false;
        }

        return true;
    }

    // main
    public static void main(String[] args) throws IOException {
        HashST<String, Integer> dictionary = loadDictionary("src/wordlist.txt", 1000); // Adjust path as needed
        String[] passwords = {"account8", "accountability", "9a$D#qW7!uX&Lv3zT", "B@k45*W!c$Y7#zR9P", "X$8vQ!mW#3Dz&Yr4K5"};

        for (String password : passwords) {
            boolean isStrong = isStrongPassword(password, dictionary);
            System.out.println("Password: " + password + " is " + (isStrong ? "strong" : "weak"));
            System.out.println("Search cost: " + dictionary.getSearchCost());
        }
    }
}
