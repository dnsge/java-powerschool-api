package org.dnsge.powerschoolapi;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;

public class User {

    private final ArrayList<ClassPeriod> classList;
    private final PowerschoolClient client;
    private final Document constructionDoc;
    private final String username;
    private final String password;
    private final String personName;

    public User(PowerschoolClient client, String username, Document doc) {
        this.client = client;
        this.constructionDoc = doc;
        this.username = username;
        this.password = (String)client.getUserData(username).get("password");
        this.classList = new ArrayList<>();

        Node usernameContainer = doc.getElementById("userName").child(0).childNode(0);
        personName = usernameContainer.toString().trim();

        Element quickLookupDoc = doc.getElementById("quickLookup");
        Element mainContentContainer = quickLookupDoc.child(0).child(0);
        for (Element child : mainContentContainer.children()) {
            if (child.hasAttr("id")) {
                ClassPeriod newPeriod = ClassPeriod.generateClassFromElement(child);
                classList.add(newPeriod);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Document getConstructionDoc() {
        return constructionDoc;
    }

    public String getPersonName() {
        return personName;
    }

    public PowerschoolClient getClient() {
        return client;
    }

    public ArrayList<ClassPeriod> getClassList() {
        return classList;
    }
}
