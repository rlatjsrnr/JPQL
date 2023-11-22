package jpql;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("회원1");
            member.setTeam(teamA);
            member.setAge(10);
            em.persist(member);

            Member member1 = new Member();
            member1.setUsername("회원2");
            member1.setTeam(teamA);
            member1.setAge(10);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원3");
            member2.setTeam(teamB);
            member2.setAge(10);
            em.persist(member2);

            /*
            // 명시적 join
            //List<Member> resultList = em.createQuery("select t from Member m join m.team t", Member.class).getResultList();

            // new jpql DTO
            //List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();

            // 페이징 처리
            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1) // 시작 인덱스
                    .setMaxResults(10) // 검색 개수
                    .getResultList();

            System.out.println("resultList = " + resultList.size());
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            // left outer join
            String query = "select m from Member m left join Team t on m.username=t.name";
            List<Member> result = em.createQuery(query, Member.class).getResultList();

            System.out.println(result.size());

            // enum type
            String query = "select m.username, 'HELLO', true from Member m where m.memberType = :userType";

            List<Object[]> resultList1 = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : resultList1) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }

            // case
            String query = "select " +
                        "case when m.age <= 10 then '학생요금' " +
                            "when m.age >= 60 then '경로요금' " +
                            "else '일반요금' " +
                        "end " +
                    "from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();

            }


            // COALESCE
            /*String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();


            // nullif
            String query = "select nullif(m.username, '관리자') from Member m";
            List<String> result = em.createQuery(query, String.class).getResultList();



            // 묵시적 내부 조인 -> 안좋다 -> 그냥 쓰지말고 명시적 조인 쓰자
            String query = "select t.members.size from Team t";

            Integer result = em.createQuery(query, Integer.class).getSingleResult();

            System.out.println("result = " + result);

            // 명시적 조인
            String query = "select m.username from Team t join t.members m";
            List resultList = em.createQuery(query).getResultList();

            System.out.println("resultList = " + resultList);

            // fetch join
            String query = "select m from Member m join fetch m.team";
            List<Member> resultList = em.createQuery(query, Member.class).getResultList();

            for (Member m : resultList) {
                System.out.println("m = " + m.getUsername() + ", " + m.getTeam().getName());
            }
           /

            String query = "select m from Member m where m.team = :team";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .setParameter("team", teamA)
                    .getResultList();

            for (Member member3 : resultList) {
                System.out.println("member3 = " + member3);
            }
             */

            // 벌크 연산을 할 경우 영속성 컨텍스트 내의 데이터를 다시 쓰면 안된다.
            // 벌크 연산은 영속성 컨텍스트를 이용하지 않고 디비에 바로 접근하기 때문이다.
            // 그렇게 때문에 벌크 연산 이후에 다시 em.find()를 해야함
            int i = em.createQuery("update Member m set m.age = 20 where m.age = 10").executeUpdate();
            System.out.println(i);
            em.clear();

            Member member3 = em.find(Member.class, member1.getId());

            System.out.println(member3.getAge());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e.getMessage());
        } finally {
            em.close();
        }
        emf.close();
    }
}
