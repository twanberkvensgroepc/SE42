package JPA;

import auction.domain.Bid;
import org.junit.Ignore;
import javax.persistence.*;
import util.DatabaseCleaner;
import auction.domain.Category;
import auction.domain.Item;
import auction.domain.User;
import auction.service.AuctionMgr;
import auction.service.RegistrationMgr;
import auction.service.SellerMgr;
import java.util.Iterator;
import java.util.Set;
import nl.fontys.util.Money;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemsFromSellerTest {

    final EntityManagerFactory emf = Persistence.createEntityManagerFactory("auctionPU");
    final EntityManager em = emf.createEntityManager();
    private AuctionMgr auctionMgr;
    private RegistrationMgr registrationMgr;
    private SellerMgr sellerMgr;

    public ItemsFromSellerTest() {
    }

    @Before
    public void setUp() throws Exception {
        registrationMgr = new RegistrationMgr();
        auctionMgr = new AuctionMgr();
        sellerMgr = new SellerMgr();
        new DatabaseCleaner(em).clean();
    }

    @Test
 //   @Ignore
    public void numberOfOfferdItems() {

        String email = "ifu1@nl";
        String omsch1 = "omsch_ifu1";
        String omsch2 = "omsch_ifu2";

        User user1 = registrationMgr.registerUser(email);
        assertEquals(0, user1.numberOfOfferedItems());

        Category cat = new Category("cat2");
        Item item1 = sellerMgr.offerItem(user1, cat, omsch1);
       
        // test number of items belonging to user1
        //assertEquals(0, user1.numberOfOfferedItems());
        assertEquals(1, user1.numberOfOfferedItems());
        /*
         *  expected: which one of te above two assertions do you expect to be true?
         *  QUESTION:
         *    Explain the result in terms of entity manager and persistance context.
        * An item cannot exist without having a user. It is a OneToMany relation, meaning
        * that if the item get's added it should also be added to the user.
         */
        assertEquals(1, item1.getSeller().numberOfOfferedItems());

        User user2 = registrationMgr.getUser(email);
        assertEquals(1, user2.numberOfOfferedItems());
        Item item2 = sellerMgr.offerItem(user2, cat, omsch2);
        assertEquals(2, user2.numberOfOfferedItems());

        User user3 = registrationMgr.getUser(email);
        assertEquals(2, user3.numberOfOfferedItems());

        User userWithItem = item2.getSeller();
        assertEquals(2, userWithItem.numberOfOfferedItems());
        //assertEquals(3, userWithItem.numberOfOfferedItems());
        /*
         *  expected: which one of te above two assertions do you expect to be true?
         *  Er worden 2 items toegevoegd aan offereditems van de user met email 'email' , user3 heeft dus deze 2 items aangeboden.
         */
        assertNotSame(user3, userWithItem);
        assertEquals(user3, userWithItem);
    }

    @Test
//    @Ignore
    public void getItemsFromSeller() {
        String email = "ifu1@nl";
        String omsch1 = "omsch_ifu1";
        String omsch2 = "omsch_ifu2";

        Category cat = new Category("cat2");

        User user10 = registrationMgr.registerUser(email);
        Item item10 = sellerMgr.offerItem(user10, cat, omsch1);
        Set<Item> it = user10.getOfferedItems();
        // testing number of items of java object
        assertTrue(it.size() > 0);
        
        // now testing number of items for same user fetched from db.
        User user11 = registrationMgr.getUser(email);
        Set<Item> it11 = user11.getOfferedItems();
        assertTrue(it11.size() > 0);
        assertFalse(it11.size() > 1);

        // Explain difference in above two tests for te iterator of 'same' user
        // The second test receives the user from the database, the first one just uses the one we just created locally.

        User user20 = registrationMgr.getUser(email);
        Item item20 = sellerMgr.offerItem(user20, cat, omsch2);
        Set<Item> it20 = user20.getOfferedItems();
        assertTrue(it20.size() > 1);


        User user30 = item20.getSeller();
        Set<Item> it30 = user30.getOfferedItems();
        assertTrue(it30.size() > 1);
    }
    
    @Test
//    @Ignore
    public void bidFromItemTest() {
        String email1 = "ifu1@nl";
        String email2 = "owijef@nl";
        String omsch1 = "omsch_ifu1";
        String omsch2 = "omsch_ifu2";

        Category cat = new Category("cat2");

        User user1 = registrationMgr.registerUser(email1);
        User user2 = registrationMgr.registerUser(email2);
        Item item1 = sellerMgr.offerItem(user1, cat, omsch1);
        Bid bid = auctionMgr.newBid(item1, user2, new Money(10, "euro"));
        Bid bid2 = auctionMgr.getItem(item1.getId()).getHighestBid();
        
        // adding a bid to item1, getting bid2 from database based on the item and comparing their users
        assertEquals(bid2.getItem().getSeller(), user1);
        
    }
}
