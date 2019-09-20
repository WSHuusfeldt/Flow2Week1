/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class Tester {
    
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        Customer c1 = new Customer("Asger", "Sørensen");
        c1.addHobbie("Studere STD");
        c1.addPhone("12345678", "Asger H.S.");
        
        em.getTransaction().begin();
        em.persist(c1);
        em.getTransaction().commit();
        
        
        
    }
    
}
