using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Xml.Linq;
using System.Xml;

namespace lab1
{
    public class ApplicationContext : DbContext
    {

        public DbSet<XmlCode> XmlCode { get; set; }
        //public ApplicationContext()
        //{
        //    Database.EnsureDeleted();
        //    Database.EnsureCreated();
        //}
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            IConfigurationRoot configuration = new ConfigurationBuilder().SetBasePath(AppDomain.CurrentDomain.BaseDirectory).AddJsonFile("appsettings.json").Build();
            optionsBuilder.UseSqlServer(configuration.GetConnectionString("LabDatabase"));
        }
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<XmlCode>().Property(x => x.Data).HasColumnType("XML");
        }
    }

    public class XmlCode
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]
        public Int64 Id  { get; set; }
        [Column(TypeName = "xml")]
        public String Data { get; set; }
        [NotMapped]
        public XElement XmlData
        {
            get { return XElement.Parse(Data); }
            set { Data = value.ToString(); }
        }
        public DateTime UpdateDate { get; set; }
    }
}
