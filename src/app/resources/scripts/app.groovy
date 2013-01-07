import com.cloudcredo.cassandra.UserRepository

get("/") {
    final repo = new UserRepository()
    repo.initData()
    final userName = repo.findUserByFirstName()

    render('index.html', [port: repo.port, url: repo.host, cassandraUsername: repo.credentials.getUsername(), user: userName])
}