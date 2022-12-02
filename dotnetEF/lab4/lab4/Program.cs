using lab1;
using Microsoft.EntityFrameworkCore;

internal class Program
{
    private static void Main(string[] args)
    {
        using (ApplicationContext db = new ApplicationContext())
        {
            Microsoft.Data.SqlClient.SqlParameter newRole = new Microsoft.Data.SqlClient.SqlParameter("@newRole", "C_TEST_FROM_VS");
            var recordsUpdated = new Microsoft.Data.SqlClient.SqlParameter
            {
                ParameterName = "@recordsUpdated",
                SqlDbType = System.Data.SqlDbType.Int,
                Direction = System.Data.ParameterDirection.Output
            };
            var errCount = new Microsoft.Data.SqlClient.SqlParameter
            {
                ParameterName = "@errorCount",
                SqlDbType = System.Data.SqlDbType.Int,
                Direction = System.Data.ParameterDirection.Output
            };
            try
            {
                db.Database.ExecuteSqlInterpolated($"exec sp_UpdateVpnRoleForMigratedClients {newRole}, {recordsUpdated} OUT, {errCount} OUT");
            }
            catch (Exception e)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine($"PANIC: {e.Message}");
                Console.ForegroundColor = ConsoleColor.White;
            }
            Console.WriteLine($"{recordsUpdated.Value} {errCount.Value}");
        }
    }
}
