using lab1;

internal class Program
{
    public static void AddUsersToDatabase(ApplicationContext applicationContext, User[] users)
    {
        foreach (var u in users)
        {
            applicationContext.User.Add(u);
        }
    }
    private static void Main(string[] args)
    {
        using (ApplicationContext db = new ApplicationContext())
        {
            User[] Users = new User[]
            {
                new User {FirstName = "John", LastName = "Doe", Login = "jd" },
                new User {FirstName = "Marty", LastName = "McFly", Login = "marty" },
                new User {FirstName = "Emmeth", LastName = "Brown", Login = "doc" },
            };

            Admin[] Admins = new Admin[]
            {
                new Admin { FirstName = "Bogdan", LastName = "Kosarevskyi", Login = "kosar", Role = Role.Admin.GlobalAdministrator },
                new Admin { FirstName = "Unknown", LastName = "Fox", Login = "fox", Role = Role.Admin.SystemAdministrator }
            };
            VpnClient[] VpnUsers = new VpnClient[]
            {
                new VpnClient { FirstName = "First", LastName = "Client", Login = "client1", Role = Role.Server.ClientGermany },
                new VpnClient { FirstName = "Second", LastName = "Client", Login = "client2",  Role = Role.Server.ClientGermany },
                new VpnClient { FirstName = "Third", LastName = "Client", Login = "client3", Role = Role.Server.ClientGermany },
                new VpnClient { FirstName = "Fourth", LastName = "Client", Login = "client4", Role = Role.Server.ClientUSA },
            };

            AddUsersToDatabase(db, Users);
            AddUsersToDatabase(db, Admins);
            AddUsersToDatabase(db, VpnUsers);
            db.SaveChanges();

            var users = db.User.ToList();
            foreach (var u in users)
            {
                Console.WriteLine(u.Login);
            }
        }
    }
}