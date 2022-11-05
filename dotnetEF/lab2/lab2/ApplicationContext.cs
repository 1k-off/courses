using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace lab2
{
    public class ApplicationContext : DbContext
    {
        public DbSet<TreeCode> TreeCode { get; set; }
        public ApplicationContext()
        {
            Database.EnsureDeleted();
            Database.EnsureCreated();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            IConfigurationRoot configuration = new ConfigurationBuilder().SetBasePath(AppDomain.CurrentDomain.BaseDirectory).AddJsonFile("appsettings.json").Build();
            optionsBuilder.UseSqlServer(configuration.GetConnectionString("LabDatabase"), conf =>
            {
                conf.UseHierarchyId();
            });
        }
    }

    public class TreeCode
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]
        public HierarchyId HierarchyId { get; set; }
        public string Name { get; set; }
    }
}
