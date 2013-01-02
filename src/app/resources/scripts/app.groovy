import com.cloudcredo.cassandra.UserRepository

get("/") {
    final repo = new UserRepository()
    repo.initData()
    final userName = repo.findUserByFirstName()

    render('index.html', [port: "1000", url: "localhost", user: userName])
}