import com.cloudcredo.config.PostgresConfig
import com.cloudcredo.domain.PostgressqlDO
import com.cloudcredo.repositories.CassandraRepository
import com.cloudcredo.repositories.PostgressqlRepository
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext

get("/") {
    final repo = new CassandraRepository()
    repo.initData()
    final userName = repo.findUserByFirstName()

    render('index.html', [port: repo.port, url: repo.host, cassandraUsername: repo.credentials.getUsername(), user: userName])
}

get("/postgresql") {

    ApplicationContext ctx = new AnnotationConfigApplicationContext(PostgresConfig.class)
    final repo = ctx.getBean(PostgressqlRepository);
    final savedDO = repo.save(new PostgressqlDO(name: "Qcon Example"))

    render('postgresql.html', [id: savedDO.id, name: savedDO.name])
}