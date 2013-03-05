package com.cloudcredo.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * @author: chris
 * @date: 05/03/2013
 */
@Entity
@Table(name = "POSTGRES_EXAMPLE")
class PostgressqlDO {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    Long id;

    @Column(name = "NAME")
    String name;

}
