package com.bankingsystem.bankingbackend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bankingsystem.bankingbackend.model.Contact;


@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
	
	
}