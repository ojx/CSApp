class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String title;
    private Organization organization;
    private Role role;
    private ThirdPartyLogin moodle;
    private ThirdPartyLogin eimacs;
    private ThirdPartyLogin aplus;
    private ThirdPartyLogin codingbat;
    private ThirdPartyLogin codehs;
    
    public User(JSONObject jsonObject) {
        this.id = (int)jsonObject.get("id");
        this.firstName = (String)jsonObject.get("first_name");
        this.lastName = (String)jsonObject.get("last_name");
        this.email = (String)jsonObject.get("email");
        this.title = (String)jsonObject.get("title");
        
        JSONObject organizationObject = (JSONObject) jsonObject.get("organization");
        JSONObject roleObject = (JSONObject) jsonObject.get("role");
        
        this.organization = new Organization((int)organizationObject.get("id"), (String)organizationObject.get("short_name"), (String)organizationObject.get("full_name"));
        this.role = new Role((int)roleObject.get("id"), (String)roleObject.get("name"));

        JSONObject thirdPartyLogins = (JSONObject)jsonObject.get("third_party_logins");

        if (thirdPartyLogins.get("moodle") != null && thirdPartyLogins.get("moodle") instanceof JSONObject) {
            JSONObject loginObject = (JSONObject) thirdPartyLogins.get("moodle");
            this.moodle = new ThirdPartyLogin((String)loginObject.get("username"), (String)loginObject.get("password"));
        }

        if (thirdPartyLogins.get("eimacs") != null && thirdPartyLogins.get("eimacs") instanceof JSONObject) {
            JSONObject loginObject = (JSONObject) thirdPartyLogins.get("eimacs");
            this.eimacs = new ThirdPartyLogin((String)loginObject.get("username"), (String)loginObject.get("password"));
        }

        if (thirdPartyLogins.get("aplus") != null && thirdPartyLogins.get("aplus") instanceof JSONObject) {
            JSONObject loginObject = (JSONObject) thirdPartyLogins.get("aplus");
            this.aplus = new ThirdPartyLogin((String)loginObject.get("username"), (String)loginObject.get("password"));
        }

        if (thirdPartyLogins.get("codingbat") != null && thirdPartyLogins.get("codingbat") instanceof JSONObject) {
            JSONObject loginObject = (JSONObject) thirdPartyLogins.get("codingbat");
            this.codingbat = new ThirdPartyLogin((String)loginObject.get("username"), (String)loginObject.get("password"));
        }

        if (thirdPartyLogins.get("codehs") != null && thirdPartyLogins.get("codehs") instanceof JSONObject) {
            JSONObject loginObject = (JSONObject) thirdPartyLogins.get("codehs");
            this.codehs = new ThirdPartyLogin((String)loginObject.get("username"), (String)loginObject.get("password"));
        }

    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return hasTitle() ? title + ". " + lastName : firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasTitle() {
        return title != null;
    }

    public String getTitle() {
        return title;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Role getRole() {
        return role;
    }

    public boolean hasMoodle() {
        return moodle != null;
    }

    public ThirdPartyLogin getMoodle() {
        return moodle;
    }

    public boolean hasEimacs() {
        return eimacs != null;
    }

    public ThirdPartyLogin getEimacs() {
        return eimacs;
    }

    public boolean hasAplus() {
        return aplus != null;
    }

    public ThirdPartyLogin getAplus() {
        return aplus;
    }

    public boolean hasCodingbat() {
        return codingbat != null;
    }

    public ThirdPartyLogin getCodingbat() {
        return codingbat;
    }

    public boolean hasCodehs() {
        return codehs != null;
    }

    public ThirdPartyLogin getCodehs() {
        return codehs;
    }
}
