import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {
    public static void main(String[] args) {
        String password = "admin123";
        String storedHash = "$2a$10$n9JxYXMm3ZHmhHO.gFJZwu/h3riNF9.WlsZtxvzG.YD1LGbmWufZi";
        
        // Generate a new hash
        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("New hash: " + newHash);
        System.out.println("New hash verification: " + BCrypt.checkpw(password, newHash));
        
        // Verify against stored hash
        System.out.println("Stored hash verification: " + BCrypt.checkpw(password, storedHash));
        
        // Generate hash with specific salt
        String specificHash = BCrypt.hashpw(password, "$2a$10$n9JxYXMm3ZHmhHO.gFJZwu");
        System.out.println("Specific salt hash: " + specificHash);
    }
} 