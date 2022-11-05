using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace lab1
{
    public class ApplicationContext : DbContext
    {
        public DbSet<User> User { get; set; } = null!;
        public DbSet<Admin> Admin { get; set; } = null!;
        public DbSet<VpnClient> VpnClient { get; set; } = null!;

        public ApplicationContext()
        {
            Database.EnsureDeleted();
            Database.EnsureCreated();
        }
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            IConfigurationRoot configuration = new ConfigurationBuilder().SetBasePath(AppDomain.CurrentDomain.BaseDirectory).AddJsonFile("appsettings.json").Build();
            optionsBuilder.UseSqlServer(configuration.GetConnectionString("LabDatabase"));
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>().ToTable("Users");
            modelBuilder.Entity<Admin>().ToTable("Admins");
            modelBuilder.Entity<VpnClient>().ToTable("VpnClients");
        }
    }
}
