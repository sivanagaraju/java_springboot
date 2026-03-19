package com.studentmanager;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Main.java | MODULE: mini-project-01                                 ║
 * ║ PURPOSE: Main execution entry point for the Application.                  ║
 * ║ WHY IT EXISTS: To boot up the UI layer and handle the lifecycle.          ║
 * ║ PYTHON COMPARE: if __name__ == "__main__":                                ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.ui.CLIHandler;

/**
 * Layer 2 - Entrypoint Level: Boots the CLI environment.
 * Python Equivalent: The root script level execution.
 */
public class Main {
    public static void main(String[] args) {
        // Layer 3: Instantiate and run the UI controller.
        new CLIHandler().start();
    }
}
