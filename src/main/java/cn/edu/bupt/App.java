package cn.edu.bupt;

import javax.swing.SwingUtilities;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.TransactionManager;
import cn.edu.bupt.model.User;
import cn.edu.bupt.view.MainFrame;

/**
 * Main application class for the Smart Bill Management System.
 * This class serves as the entry point for the application.
 */
public class App {
    /** Synchronization object for managing application lifecycle */
    public static Object lock = new Object();
    
    /**
     * Main method to start the application.
     * Initializes data from CSV files, sets the default user,
     * launches the UI, and waits for application termination.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // 从csv中读取
        CsvTransactionDao.readAllCSV();
        TransactionManager.getInstance().currentUser = User.defaultUser;

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(lock);
        });

        // 等待主窗口关闭
        synchronized(lock) {
            try {
                lock.wait(); // 阻塞直到被唤醒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
