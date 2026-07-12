package com.mcotools.repository;

import com.mcotools.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByDepositId(String depositId);

    List<Request> findByEtat(String etat);

    List<Request> findByCanal(String canal);

    List<Request> findByLogin(String login);
}
