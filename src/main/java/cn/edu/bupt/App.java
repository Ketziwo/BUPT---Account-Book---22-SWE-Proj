package cn.edu.bupt;

import javax.swing.SwingUtilities;

import cn.edu.bupt.dao.CsvTransactionDao;
import cn.edu.bupt.model.TransactionManager;
import cn.edu.bupt.model.User;
import cn.edu.bupt.view.MainFrame;

public class App {
    public static Object lock = new Object();
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
