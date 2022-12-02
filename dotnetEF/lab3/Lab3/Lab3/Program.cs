using lab1;
using System.Linq;
using System.Reflection.Emit;
using System.Xml.Linq;
using System.Xml.XPath;

internal class Program
{
    private static void Main(string[] args)
    {
        using (ApplicationContext db = new ApplicationContext())
        {
            XDocument docFile = XDocument.Load("./sites.xml");
            XElement root = docFile.Root;

            //var data = new XmlCode()
            //{
            //    XmlData = root
            //};
            //db.XmlCode.Add(data);
            //db.SaveChanges();
            var entity = db.XmlCode.FirstOrDefault();
            var doc = entity.XmlData;
            //Console.WriteLine("TASK 1");
            //Console.WriteLine("Query one xml node");
            //Console.WriteLine(doc.XPathSelectElement("/SITE/site[@name = \"gs2trello.ukad.dev\"]"));

            //Console.WriteLine("Query all bindingInformations");
            //foreach (var element in doc.XPathSelectElements("/SITE/site/bindings/binding"))
            //{
            //    Console.WriteLine(element.Attribute("bindingInformation").Value);
            //}

            /*
            DECLARE @domain NVARCHAR(MAX) = '1node.xyz'
            DECLARE @httpBinding NVARCHAR(MAX) = '*:80:1node.xyz'
            DECLARE @httpsBinding NVARCHAR(MAX) = '*:443:1node.xyz'
            UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/@name)[1] with sql:variable("@domain")')
            UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/bindings/binding/@bindingInformation)[1] with sql:variable("@httpBinding")')
            UPDATE [dbo].[IISData] SET [Data].modify ('replace value of (/appcmd/SITE/site/bindings/binding/@bindingInformation)[2] with sql:variable("@httpsBinding")')
            */
            Console.WriteLine("Update default website information");
            var domain = "1node.xyz";
            var httpBinding = "*:80:1node.xyz";
            var httpsBinding = "*:443:1node.xyz";


            
            var @default = doc.Element("SITE")?.Elements("site").FirstOrDefault(s => s.Attribute("name")?.Value == "Default Web Site");
            Console.WriteLine(@default);
            if (@default != null)
            {
                var name = @default.Attribute("name");
                if (name != null) name.Value = domain;
                var binding = @default.Elements("bindings").Elements("binding").ToList();
                binding[0].Attribute("bindingInformation").SetValue(httpBinding);
                binding[1].Attribute("bindingInformation").SetValue(httpsBinding);
            }
            entity.XmlData = doc;
            db.XmlCode.Update(entity);
            db.SaveChanges();

        }
    }
}

