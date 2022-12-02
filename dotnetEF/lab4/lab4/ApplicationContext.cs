using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace lab1
{
    public class ApplicationContext : DbContext
    {
        public DbSet<User> User { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            IConfigurationRoot configuration = new ConfigurationBuilder().SetBasePath(AppDomain.CurrentDomain.BaseDirectory).AddJsonFile("appsettings.json").Build();
            optionsBuilder.UseSqlServer(configuration.GetConnectionString("LabDatabase"));
        }
    }
}
