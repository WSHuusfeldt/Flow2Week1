package facades;

import entities.Address;
import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //TODO Remove/Change this before use
    public long getRenameMeCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long renameMeCount = (long) em.createQuery("SELECT COUNT(r) FROM RenameMe r").getSingleResult();
            return renameMeCount;
        } finally {
            em.close();
        }

    }

    public Person addPerson(String fName, String lName, String phone, String street, String zip, String city) {
        EntityManager em = emf.createEntityManager();
        Person p;
        try {
            List<Address> query = em.createQuery("SELECT a FROM Address a where a.street = :street AND a.zip = :zip AND a.city = :city", Address.class)
                    .setParameter("street", street)
                    .setParameter("zip", zip)
                    .setParameter("city", city)
                    .getResultList();
            if (query.size() > 0) {
                p = new Person(fName, lName, phone, query.get(0));
            } else {
                p = new Person(fName, lName, phone, new Address(street, zip, city));
            }
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    public Person editPerson(Person p) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Person check = em.find(Person.class, p.getId());
            if(check == null) {
                throw new PersonNotFoundException("Person not found");
            }
            p.setLastEdited(new Date());
            em.getTransaction().begin();
            p = em.merge(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    public Person getPerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person p;
        try {
            em.getTransaction().begin();
            p = em.find(Person.class, id);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return p;
    }

    public List<Person> getAllPeople() throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
            if (query.getResultList() == null) {
                throw new PersonNotFoundException("Person not found");
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Person deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person p;
        try {
            p = em.find(Person.class, id);
            if (p == null) {
                throw new PersonNotFoundException("Person was not found");
            }
            em.getTransaction().begin();
            
            em.remove(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

}
