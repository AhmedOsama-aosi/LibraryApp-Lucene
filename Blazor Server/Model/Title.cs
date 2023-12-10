using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class Title
{
    public int Bookid { get; set; }

    public int Id { get; set; }

    public string? Tit { get; set; }

    public short? Lvl { get; set; }

    public short? Sub { get; set; }

    public int Rowid { get; set; }

    public virtual _0bok Book { get; set; } = null!;
}
