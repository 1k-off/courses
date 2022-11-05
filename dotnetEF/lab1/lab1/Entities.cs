namespace lab1
{
    public class User
    {
        public int Id { get; set; }
        public string? FirstName { get; set; }
        public string? LastName { get; set; }
        public string Login { get; set; }

    }

    public class Admin : User
    {
        public string Role { get; set; }
    }

    public class VpnClient : User
    {
        public string Role { get; set; }
    }

    public class Role 
    {
        public class Admin
        {
            public const string GlobalAdministrator = "Global Administrator";
            public const string SystemAdministrator = "Syatem Administrator";
        }
        public class Server
        {
            public const string ClientGermany = "C_DE";
            public const string ClientUSA = "C_USA";
            public const string AdminUkraine = "A_UA";
        }
    }
}
