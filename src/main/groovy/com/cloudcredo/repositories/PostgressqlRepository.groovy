package com.cloudcredo.repositories

import com.cloudcredo.domain.PostgressqlDO
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * @author: chris
 * @date: 05/03/2013
 */
@Repository
interface PostgressqlRepository extends CrudRepository<PostgressqlDO, Long> {

}
