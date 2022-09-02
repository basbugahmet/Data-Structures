import java.util.*;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {

    //not values for indices here are long because some index numbers are so large and it leads to overflow.

    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 5; // Must be prime
    private static final int MAX_CAPACITY = 1000000;
    int probeType = 0; //linear probing or double hashing
    int collisionCount = 0; //number of collisions
    int hashFunctionType = 0; //SSF or PAF
    boolean rehash = false;

    // The hash table:
    private TableEntry<K, V>[] hashTable;
    private int tableSize; // Must be prime
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private boolean initialized = false;
    private static double MAX_LOAD_FACTOR = 0.5; // Fraction of hash table


    public HashedDictionary(int initialCapacity, int probeTypeInput, int hashFunctionTypeInput, int loadFactorInput) throws Exception {

        checkCapacity(initialCapacity); //checks if the maximum capacity is exceeded
        numberOfEntries = 0; // Dictionary is empty
        probeType = probeTypeInput; //if 1 then linear probing ;  if 2 then double hashing
        hashFunctionType = hashFunctionTypeInput; //if 1 then SSM ;  if 2 then PAF

        if(loadFactorInput == 1){
            MAX_LOAD_FACTOR = 0.5;
        }
        else if(loadFactorInput == 2){
            MAX_LOAD_FACTOR = 0.8;
        }

        // Set up hash table:
        // Initial size of hash table is same as initialCapacity if it is prime;
// otherwise increase it until it is prime size
        int tableSize = getNextPrime(initialCapacity);
        checkSize(tableSize); // Check for max array size, this is different from checkCapacity()

// The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[tableSize];
        hashTable = temp;
        initialized = true; //created
    } // end constructor



    @Override
    public V put(K key, V value) {
        checkInitialization();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException();
        else {
            V oldValue; // Value to return
            long index = getHashIndex(key);
            if(probeType == 1){
                index = probeForLinearHashing(index, key); // Check for and resolve collision with linear probing
            }
            else if(probeType == 2){
                index = probeForDoubleHashing(index, key); // Check for and resolve collision with double hashing
            }
            // Assertion: index is within legal range for hashTable
            assert (index >= 0) && (index < hashTable.length);
            if ((hashTable[(int) index] == null) || hashTable[(int) index].isRemoved()) { // Key not found, so insert new entry
                hashTable[(int) index] = new TableEntry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            } else { // Key found; get old value for return and then replace it
                oldValue = hashTable[(int) index].getValue();
                hashTable[(int) index].setValue(value);
            } // end if
            // Ensure that hash table is large enough for another add
            if (isHashTableTooFull()){//max capacity exceeded or not
                int oldSize = hashTable.length;
                int newSize = getNextPrime(oldSize + oldSize);
                resize(newSize);
            }
            return oldValue;
        } // end if
    } // end add


    @Override
    public V remove(K key) {
        checkInitialization();
        V removedValue = null;

        long index = getHashIndex(key);

        if(probeType == 1){
            index = locateForLinearProbing(index, key); // Check for and resolve collision with linear probing
        }
        else if(probeType == 2){
            index = locateForDoubleHashing(index, key); // Check for and resolve collision with double hashing
        }
        if (index != -1) { // Key found; flag entry as removed and return its value
            removedValue = hashTable[(int) index].getValue();
            hashTable[(int) index].setToRemoved();
            numberOfEntries--;
        } // end if
        // Else key not found; return null
        return removedValue;
    } // end remove


    @Override
    public V get(K key) {
        checkInitialization();
        V result = null;
        long index = getHashIndex(key);
        if(probeType == 1){
            index = locateForLinearProbing(index, key); // Check for and resolve collision with linear probing
        }
        else if(probeType == 2){
            index = locateForDoubleHashing(index, key); // Check for and resolve collision with double hashing
        }
        if (index != -1)
            result = hashTable[(int) index].getValue(); // Key found; get value
        // Else key not found; return null
        return result;
    } // end getValue


    @Override
    public boolean contains(K key) {
        if (get(key) != null) {
            return true;
        }
        return false;
    }


    @Override
    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }

    @Override
    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    @Override
    public void clear() {
        for (int i = 0; i < numberOfEntries; i++) {
            hashTable[i] = null;
        }
        numberOfEntries = 0;
    }


    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a bag whose " +
                    "capacity exceeds allowed " +
                    "maximum of " + MAX_CAPACITY);
    } // end checkCapacity


    private int getNextPrime(int num) {
        while(!isPrimeNumber(num))
            ++num;
        return num;
    }
    private boolean isPrimeNumber(int num) { //also it can be coded as according to square root of num.
        if(num == 1 || num==0 || num<0){
            return false;
        }
        for(int i = 2; i < num; i++) //i<sqrt(num) in comparison
            if(num % i == 0)
                return false;
        return true;
    }


    private void checkSize(int tableSize) throws Exception {
        if (tableSize > MAX_SIZE) {
            throw new Exception("The size of the table is too large!");
        }
    }


    private void checkInitialization() {
        if (!initialized)
            throw new SecurityException("Object is not initialized properly.");
    } // end checkInitialization


    private long probeForLinearHashing(long index, K key) {
        boolean found = false;
        long removedStateIndex = -1; // Index of first location in removed state
        while (!found && (hashTable[(int) index] != null)) {
            if (hashTable[(int) index].isIn()) { //current
                if (key.equals(hashTable[(int) index].getKey()))
                    found = true; // Key found
                else{ // Follow probe sequence
                    index = (index + 1) % hashTable.length;// Linear probing
                    if(!rehash){ //if not resized hash table
                        collisionCount++; //increasing collision number for just first adding not rehashing
                    }
                }
            }else // Skip entries that were removed //removed
            {
                // Save index of first location in removed state
                if (removedStateIndex == -1)
                    removedStateIndex = index;
                index = (index + 1) % hashTable.length; // Linear probing
            } // end if
        } // end while
        // Assertion: Either key or null is found at hashTable[index]
        if (found || (removedStateIndex == -1)){

            return index;} // Index of either key or null
        else
            return removedStateIndex; // Index of an available location
    } // end probe


    private long probeForDoubleHashing(long index, K key) {
        boolean found = false;
        long removedStateIndex = -1; // Index of first location in removed state
        long index2 = 0;
        if(hashFunctionType == 1){
            index2 = getHashIndexForDoubleHashingSSF(key); //h2 function
        }else if(hashFunctionType == 2){
            index2 = getHashIndexForDoubleHashingPAF(key); //h2 function
        }
        int i = 1; //We do not need to start from 0 here. Because above, it can be said that the index i=0 came as zero.
        long newIndex = index;
        while (!found && (hashTable[(int) newIndex] != null)) {
            if (hashTable[(int) newIndex].isIn()) {
                if (key.equals(hashTable[(int) newIndex].getKey()))
                    found = true; // Key found
                else{ // Follow probe sequence
                    newIndex = (index + i * index2 ) % hashTable.length; //double hashing function
                    if(!rehash){
                        collisionCount++;
                    }
                    i++;}// Linear probing
            } else // Skip entries that were removed
            {
                // Save index of first location in removed state
                if (removedStateIndex == -1)
                    removedStateIndex = newIndex;
                newIndex = (index + i * index2) % hashTable.length;
                i++;// Linear probing
            } // end if
        } // end while
        // Assertion: Either key or null is found at hashTable[index]
        if (found || (removedStateIndex == -1))
            return newIndex; // Index of either key or null
        else
            return removedStateIndex; // Index of an available location
    } // end probe


    private boolean isHashTableTooFull() { //load factor is exceeded or not
        return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
    }

    private void resize(int capacity) { //if the hashtable too full, in other words load factor exceeded
        TableEntry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = capacity;
        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[newSize];
        hashTable = temp;
        numberOfEntries = 0; // Reset number of dictionary entries, since
        // it will be incremented by add during rehash
        // Rehash dictionary entries from old array to the new and bigger
        // array; skip both null locations and removed entries
        for (int index = 0; index < oldSize; index++) {
            if ((oldTable[index] != null) && oldTable[index].isIn()){
                rehash = true;
                put(oldTable[index].getKey(), oldTable[index].getValue());
                rehash = false;
            }
        } // end for
    } // end resize()


    private long locateForLinearProbing(long index, K key) {
        boolean found = false;
        while (!found && (hashTable[(int) index] != null)) {
            if (hashTable[(int) index].isIn() &&
                    key.equals(hashTable[(int) index].getKey()))
                found = true; // Key found
            else // Follow probe sequence
            {
                index = (index + 1) % hashTable.length;} // Linear probing
        } // end while
        // Assertion: Either key or null is found at hashTable[index]
        long result = -1;
        if (found)
            result = index;
        return result;
    } // end locate


    private long locateForDoubleHashing(long index, K key) {
        boolean found = false;
        long index2 = 0;
        if(hashFunctionType == 1){
            index2 = getHashIndexForDoubleHashingSSF(key);
        }else if(hashFunctionType == 2){
            index2 = getHashIndexForDoubleHashingPAF(key);
        }
        int i = 1;
        long newIndex = index;
        while (!found && (hashTable[(int) newIndex] != null)) {
            if (hashTable[(int) newIndex].isIn() &&
                    key.equals(hashTable[(int) newIndex].getKey()))
                found = true; // Key found
            else // Follow probe sequence
            {
                newIndex = (index + i * index2) % hashTable.length;
                i++;}// Linear probing // Linear probing
        } // end while
        // Assertion: Either key or null is found at hashTable[index]
        long result = -1;
        if (found)
            result = newIndex;
        return result;
    } // end locate


    public int collisionCountFunc(){
        return collisionCount;
    }

    private class KeyIterator implements Iterator<K> {
        private int currentIndex; // Current position in hash table
        private int NumberLeft; // Number of entries left in iteration

        private KeyIterator() {
            currentIndex = 0;
            NumberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext() {
            return NumberLeft > 0;
        } // end hasNext

        public K next() {
            K result = null;
            if (hasNext()) {
                // Skip table locations that do not contain a current entry
                while ((hashTable[currentIndex] == null) ||
                        hashTable[currentIndex].isRemoved()) {
                    currentIndex++;
                } // end while
                result = hashTable[currentIndex].getKey();
                NumberLeft--;
                currentIndex++;
            } else
                throw new NoSuchElementException();
            return result;
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    } // end KeyIterator


    private class ValueIterator implements Iterator<V> {
        private int currentIndex; // Current position in hash table
        private int NumberLeft; // Number of entries left in iteration

        private ValueIterator() {
            currentIndex = 0;
            NumberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext() {
            return NumberLeft > 0;
        } // end hasNext

        public V next() {
            V result = null;
            if (hasNext()) {
                // Skip table locations that do not contain a current entry
                while ((hashTable[currentIndex] == null) ||
                        hashTable[currentIndex].isRemoved()) {
                    currentIndex++;
                } // end while
                result = hashTable[currentIndex].getValue();
                NumberLeft--;
                currentIndex++;
            } else
                throw new NoSuchElementException();
            return result;
        } // end next

        public void remove() {
            throw new UnsupportedOperationException();
        } // end remove
    } // end Value Iterator



    private long hashCodeAccordingToPAF(K key) {
        String entry = (String) key;
        int constant = 31;
        long hash = 0;
        int n = entry.length();
        for (int i = 0; i < n; i++)
            hash = constant * hash + (entry.charAt(i)-96);
        //entry.charAt(i) will give us ASCII value, if we substract 96 from it then will give us places in English alphabet
        //since capital letters are not case in this project, this logic will work.
        return hash;
    }

    private int hashCodeAccordingToSSF(K key) {
        String entry = (String) key;
        int hash = 0;
        int n = entry.length();
        for (int i = 0; i < n; i++)
            hash =  hash + (entry.charAt(i)-96); //same logic above
        return hash;
    }


    private long getHashIndex(K key) { //this function can be considered as h1 function
        long hashIndex = 0;
        if(hashFunctionType == 1){
            hashIndex = hashCodeAccordingToSSF(key) % hashTable.length;
        }else if(hashFunctionType == 2){
            hashIndex =  hashCodeAccordingToPAF(key) % hashTable.length;
        }
        if (hashIndex < 0)
            hashIndex = hashIndex + hashTable.length;

        return hashIndex;
    } // end getHashIndex


    private long getHashIndexForDoubleHashingPAF(K key) {  //this function can be considered as h2 function
        long hashIndex = hashCodeAccordingToPAF(key);
        return 71 - (hashIndex % 71); //used 71 here because it provides well distribution and less collision
    }

    private long getHashIndexForDoubleHashingSSF(K key) { //this function can be considered as h2 function
        long hashIndex = hashCodeAccordingToSSF(key);
        return 71 - (hashIndex % 71);//used 71 here because it provides well distribution and less collision
    }


    public V search(K key) { //return value of key
        if (get(key) != null) {
            return get(key);
        }
        return null;
    }

    static class TableEntry<S, T> {

        private S key;
        private T value;
        private States state; // Flags whether this entry is in the hash table

        enum States {CURRENT, REMOVED} // Possible values of state

        private TableEntry(S searchKey, T dataValue) {
            key = searchKey;
            value = dataValue;
            state = States.CURRENT;
        } // end constructor

        public S getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }


        private boolean isIn() {
            return state == States.CURRENT;
        }


        public boolean isRemoved() {
            return state == States.REMOVED;
        }

        private void setToRemoved() {
            key = null;
            value = null;
            state = States.REMOVED;
        } // end setToRemoved

    }

}




