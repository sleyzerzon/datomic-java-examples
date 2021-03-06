package datomic.samples;

import datomic.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static datomic.Util.list;
import static datomic.Util.map;
import static datomic.samples.Fns.scratchConnection;
import static datomic.samples.IO.resource;
import static datomic.samples.IO.transactAll;

import static datomic.Peer.*;

public class DatabaseFiltering {
    public static Object tempid() {
        return Peer.tempid("db.part/user");
    }

    public static final String storyQuery = "[:find (count ?e) :where [?e :story/url]]";

    public static void main(String[] args) throws IOException {
        Connection conn = scratchConnection();
        URL url = resource("datomic-java-examples/social-news.dtm");
        transactAll(conn, new InputStreamReader(url.openStream()));
        Database db = conn.db();

        System.out.print("\nTotal number of stories: ");
        System.out.println(q(storyQuery, db));

        System.out.print("\nTotal number of published stories: ");
        System.out.println(q(storyQuery, db.filter(new Database.Predicate<datomic.Datom>() {
            public boolean apply(Database db, Datom datom) {
                return db.entity(datom.tx()).get(":publish/at") != null;
            }
        })));
    }
}
