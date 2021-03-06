package auction.service;

import auction.dao.ItemDAO;
import auction.dao.ItemDAOJPAImpl;
import auction.dao.UserDAO;
import auction.dao.UserDAOJPAImpl;
import auction.domain.Category;
import auction.domain.Item;
import auction.domain.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SellerMgr {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("auctionPU");
    /**
     * @param seller
     * @param cat
     * @param description
     * @return het item aangeboden door seller, behorende tot de categorie cat
     *         en met de beschrijving description
     */
    public Item offerItem(User seller, Category cat, String description) {
        EntityManager em = emf.createEntityManager();
        ItemDAO itemDAO = new ItemDAOJPAImpl(em);
        UserDAO userDAO = new UserDAOJPAImpl(em);
        
        Item item;
        item = seller.createItem(cat, description);
        em.getTransaction().begin();
        try {
            itemDAO.create(item);
            userDAO.edit(seller);
            em.getTransaction().commit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            item = null;
        }
        finally {
            em.close();
        }
        return item;
    }
    
     /**
     * @param item
     * @return true als er nog niet geboden is op het item. Het item word verwijderd.
     *         false als er al geboden was op het item.
     */
    public boolean revokeItem(Item item) {
        EntityManager em = emf.createEntityManager();
        ItemDAO itemDAO = new ItemDAOJPAImpl(em);
        UserDAO userDAO = new UserDAOJPAImpl(em);
        em.getTransaction().begin();
        boolean bids = true;
        try {
            item = itemDAO.find(item.getId());
            if (item.getHighestBid() == null)
            {
                bids = false;
                itemDAO.remove(item);
                userDAO.edit(item.getSeller());
            }
            em.getTransaction().commit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
        }
        finally {
            em.close();
        }
        return !bids;
    }
}
