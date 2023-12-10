using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using System.Configuration;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;




var builder = WebApplication.CreateBuilder(args);
// Add services to the container.
builder.Services.AddRazorPages();
builder.Services.AddServerSideBlazor();




builder.Services.AddServerSideBlazor().AddHubOptions(options => { options.MaximumReceiveMessageSize = 512 * 1024*1024;});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();

app.UseStaticFiles();

app.UseRouting();

app.MapBlazorHub();
app.MapFallbackToPage("/_Host");

try
{
  
   
    String serverHostName = Dns.GetHostName();
	IPHostEntry ipEntry = Dns.GetHostEntry(serverHostName);
	IPAddress[] addr = ipEntry.AddressList;
	string pip = "";
	foreach (IPAddress ip in addr)
	{
		if (ip.AddressFamily == AddressFamily.InterNetwork && ip.ToString().StartsWith("192.168.0"))
		{
			pip = "https://" + ip.ToString() + ":3000";
			break;
		}
	}
	if (pip != "")
	{
		app.Urls.Add(pip);
	}
	else
	{
		pip = "https://localhost:5000";
		app.Urls.Add(pip);
	}
	using (LibraryApp.Model.LibraryDbContext db = new LibraryApp.Model.LibraryDbContext())
	{
		int max = 0;
		max = db._0cats.Max(b => b.Id);
	}
	Process.Start(new ProcessStartInfo(pip) { UseShellExecute = true });
 // string path = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

	//Process.Start(new ProcessStartInfo()
	//{
	//	UseShellExecute = false,
	//	FileName = path,
	//	Arguments = "--new-window "+pip,
	//});
}

catch (Exception other)
{
    Console.WriteLine(other.Message);
	Console.ReadKey();
}
app.Run();


//Scaffold-DbContext "Server=(localdb)\ProjectModels;Database=dbt;Trusted_Connection=True;" Microsoft.EntityFrameworkCore.SqlServer -OutputDir Model
//Scaffold-DbContext "Server=localhost\SQLEXPRESS;Database=db;Trusted_Connection=True;Encrypt=False;" Microsoft.EntityFrameworkCore.SqlServer -OutputDir Model
//dotnet ef dbcontext scaffold "data source=D:\shamela4_2\database\master.db" Microsoft.EntityFrameworkCore.Sqlite --output-dir SQLite
//  => optionsBuilder.UseSqlServer($"Data Source=localhost\\SQLEXPRESS; AttachDbFilename={Environment.CurrentDirectory}\\database\\db.mdf; Integrated Security=True; Connect Timeout=30;");



//=> optionsBuilder.UseSqlServer("Server=localhost\\SQLEXPRESS;Database=db;Trusted_Connection=True;Encrypt=False;");
// => optionsBuilder.UseSqlServer("Data Source=(localdb)\\MSSQLLocalDB;Integrated Security=true;AttachDbFileName=C:\\Users\\A.osama\\Desktop\\copy data\\db.mdf;", options => options.EnableRetryOnFailure());
//  => optionsBuilder.UseSqlServer($"Data Source=.\\SQLEXPRESS; AttachDbFileName={Environment.CurrentDirectory}\\database\\dbt.mdf;Trusted_Connection=True; Encrypt=False;");
//  => optionsBuilder.UseSqlServer($"Data Source=.\\SQLEXPRESS; AttachDbFileName=D:\\Projects\\libraryApp\\database\\dbt.mdf;Trusted_Connection=True; Encrypt=False;");