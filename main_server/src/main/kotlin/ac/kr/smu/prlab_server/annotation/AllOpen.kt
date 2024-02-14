package ac.kr.smu.prlab_server.annotation
//참고 : https://v3.leedo.me/devs/81
/*
    JPA의 LAZY 클래스는 proxy 객체를 이용해 구현되기 때문에
    클래스는 상속을 허용해야해 AllOpen을 해줘야한다
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AllOpen
