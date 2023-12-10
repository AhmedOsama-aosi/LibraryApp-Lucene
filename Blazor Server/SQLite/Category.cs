using System;
using System.Collections.Generic;

namespace LibraryApp.SQLite;

public partial class Category
{
    public long CategoryId { get; set; }

    public string? CategoryName { get; set; }

    public long? CategoryOrder { get; set; }
}
