using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class User
{
    public short Rowid { get; set; }

    public int? Bookmark { get; set; }

    public int? Lastaccsss { get; set; }

    public string? Lastsearch { get; set; }


}
