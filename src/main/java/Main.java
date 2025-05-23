import model.Account;

import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        Account a1 = new Account("1231243123",new BigDecimal(1000));
        Account a2 = new Account("12222222",new BigDecimal(2000));
        // Spremanje računa u bazu
        savaAccount(a1);
        savaAccount(a2);
        // Spremanje računa u bazu
        new Thread( () -> transferMoney(a1,a2, new BigDecimal(1500))).start();
        new Thread( () -> transferMoney(a2,a1,new BigDecimal(300))).start();


    }

    public static void transferMoney(Account source, Account target, BigDecimal amount) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            //simuliranje čekanja prije ažuriranja
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // ovo ažuriranje može dovesti do deadlocka
            source.setBalance(source.getBalance().subtract(amount));
            target.setBalance(target.getBalance().add(amount));
            session.update(source);
            session.update(target);
            transaction.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void savaAccount(Account account) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
        }
    }
}
