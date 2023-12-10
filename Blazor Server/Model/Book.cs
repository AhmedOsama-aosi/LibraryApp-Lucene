using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class Book
{
    public string Nass { get; set; } = null!;

    public byte? Sora { get; set; }

    public int Id { get; set; }

    public short? Part { get; set; }

    public int? Page { get; set; }

    public byte? Hno { get; set; }

    public int? Aya { get; set; }

    public byte? Na { get; set; }

    public int Bookid { get; set; }

    public int? Gn { get; set; }

    public string? Seal { get; set; }

    public int? Bhno { get; set; }

    public int? Ppart1 { get; set; }

    public int? Ppage1 { get; set; }

    public int? B1 { get; set; }

    public int Rowid { get; set; }

    public virtual _0bok BookNavigation { get; set; } = null!;
}
