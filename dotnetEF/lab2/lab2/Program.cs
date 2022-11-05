using lab2;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
internal class Program
{
    private static async Task Main(string[] args)
    {
        using (ApplicationContext db = new ApplicationContext())
        {
            db.TreeCode.AddRange(
                GetInitValues()
            );
            db.SaveChanges();

            Console.WriteLine("TASK 2: GET A BRANCH OF TREE STARTING FROM THE GIVEN NODE");
            foreach (var n in GetBranchWithNode(db, "AA"))
            {
                Console.WriteLine(n);
            }

            Console.WriteLine("TASK 3: SELECT ALL PARENTS OF A GIVEN NODE");
            foreach (var n in GetAllParentsOfNode(db, "ABBA"))
            {
                Console.WriteLine(n);
            }

            DeleteTreeBranchContainingNode(db, "AA");
        }
    }

    private static List<TreeCode> GetInitValues()
    {
        var Root = new TreeCode { HierarchyId = HierarchyId.Parse("/"), Name = "A" };
        var AB = new TreeCode { HierarchyId = Root.HierarchyId.GetDescendant(null, null), Name = "AB" };
        var AA = new TreeCode { HierarchyId = Root.HierarchyId.GetDescendant(null, AB.HierarchyId), Name = "AA" };
        var AAA = new TreeCode { HierarchyId = AA.HierarchyId.GetDescendant(null, null), Name = "AAA" };
        var AAB = new TreeCode { HierarchyId = AA.HierarchyId.GetDescendant(AAA.HierarchyId, null), Name = "AAB" };
        var ABB = new TreeCode { HierarchyId = AB.HierarchyId.GetDescendant(null, null), Name = "ABB" };
        var ABA = new TreeCode { HierarchyId = AB.HierarchyId.GetDescendant(null, ABB.HierarchyId), Name = "ABA" };
        var ABC = new TreeCode { HierarchyId = AB.HierarchyId.GetDescendant(ABB.HierarchyId, null), Name = "ABC" };
        var ABBA = new TreeCode { HierarchyId = ABB.HierarchyId.GetDescendant(null, null), Name = "ABBA" };
        var ABBB = new TreeCode { HierarchyId = ABB.HierarchyId.GetDescendant(ABBA.HierarchyId, null), Name = "ABBA" };
        return new List<TreeCode> { Root, AB, AA, AAA, AAB, ABB, ABA, ABC, ABBA, ABBB };
    }

    public static List<string> GetBranchWithNode(ApplicationContext db, string name) 
    {
        var nodeId = db.TreeCode.Where(t => t.Name == name).FirstOrDefault();
        return db.TreeCode.Where(t => t.HierarchyId.IsDescendantOf(nodeId.HierarchyId)).Select(t => t.Name).ToList();
    }

    public static List<string> GetAllParentsOfNode(ApplicationContext db, string name)
    {
        var nodeId = db.TreeCode.Where(t => t.Name == name).FirstOrDefault();
        var result = db.TreeCode.Where(t => nodeId.HierarchyId.IsDescendantOf(t.HierarchyId)).Select(t => t.Name).ToList();
        return result;
    }

    public static void DeleteTreeBranchContainingNode(ApplicationContext db, string name)
    {
        var nodeId = db.TreeCode.Where(t => t.Name == name).FirstOrDefault();
        var searchResult = db.TreeCode.Where(t => t.HierarchyId.IsDescendantOf(nodeId.HierarchyId)).ToList();
        db.TreeCode.RemoveRange(searchResult);
        db.SaveChanges();
    }
}
