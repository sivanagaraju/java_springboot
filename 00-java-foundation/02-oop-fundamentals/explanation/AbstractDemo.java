package com.learning.oop;

/**
 * Demonstrates Abstraction using Abstract Classes and Interfaces.
 * Shows when to use which structure depending on "Identity" vs "Capability".
 */

// 1. Abstract Class (Identity - "Is-A")
// Defines the core identity and shared state of being a Document
abstract class Document {
    protected String title;

    public Document(String title) {
        this.title = title;
    }

    // Concrete method: All documents share this exact behavior
    public void printTitle() {
        System.out.println("Document Title: " + title);
    }

    // Abstract method: Every specific document format must figure out HOW to render itself
    public abstract void render(); 
}

// 2. Interface (Capability - "Can-Do")
// Anything can be encryptable: a Document, a HardDrive, a NetworkStream...
interface Encryptable {
    // Implicitly public abstract
    void encrypt();
    void decrypt();

    // Default method (Java 8+)
    default void getSecurityStandard() {
        System.out.println("Standard: AES-256");
    }
}

// 3. Concrete Classes

// Represents a PDF: "Is-A" Document, and it "Can-Do" Encryption
class PDFDocument extends Document implements Encryptable {

    public PDFDocument(String title) {
        super(title);
    }

    @Override
    public void render() {
        System.out.println("Rendering PDF using Adobe Engine...");
    }

    @Override
    public void encrypt() {
        System.out.println("Applying PDF password protection...");
    }

    @Override
    public void decrypt() {
        System.out.println("Removing PDF password protection...");
    }
}

// Represents a TXT: "Is-A" Document, but it CANNOT do Encryption.
class TextDocument extends Document {

    public TextDocument(String title) {
        super(title);
    }

    @Override
    public void render() {
        System.out.println("Displaying raw text to console...");
    }
}


public class AbstractDemo {
    public static void main(String[] args) {
        
        System.out.println("--- 1. Using Documents (Abstract Class View) ---");
        // Document doc = new Document("Blank");  // ERROR: Cannot instantiate abstract class!
        
        Document myPdf = new PDFDocument("AnnualReport.pdf");
        Document myTxt = new TextDocument("notes.txt");

        // We can call these safely because Document forces them to exist
        myPdf.printTitle();
        myPdf.render();

        myTxt.printTitle();
        myTxt.render();

        System.out.println("\n--- 2. Using Interfaces (Capability View) ---");
        
        // We know myPdf is Encryptable, but myTxt is not.
        // We can typecast it, or reference it via its Interface type.
        Encryptable secureFile = (Encryptable) myPdf;
        secureFile.getSecurityStandard();
        secureFile.encrypt();
    }
}
