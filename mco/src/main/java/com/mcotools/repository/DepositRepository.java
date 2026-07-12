package com.mcotools.repository;

import com.mcotools.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {

    List<Deposit> findByPartner(String partner);

    List<Deposit> findByStatut(String statut);

    List<Deposit> findByLogin(String login);
}
