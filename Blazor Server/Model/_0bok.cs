using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class _0bok
{
    public int Bkid { get; set; }

    public string Bk { get; set; } = null!;

    public int? Cat { get; set; }

    public string? Betaka { get; set; }

    public string? Inf { get; set; }

    public int? Bkord { get; set; }

    public int? Authno { get; set; }

    public string? Auth { get; set; }

    public string? AuthInf { get; set; }

    public int? Max { get; set; }

    public byte? NoSr { get; set; }

    public byte? Comp { get; set; }

    public byte? IslamShort { get; set; }

    public string? TafseerNam { get; set; }

    public byte? Idx { get; set; }

    public byte? Archive { get; set; }

    public string? Iso { get; set; }

    public int? ONum { get; set; }

    public int? OVer { get; set; }

    public int? BVer { get; set; }

    public string? Seal { get; set; }

    public int? OAuth { get; set; }

    public byte? Pdf { get; set; }

    public byte? NoSel { get; set; }

    public byte? NoTaf { get; set; }

    public string? VerName { get; set; }

    public string? BLnk { get; set; }

    public byte? PdfCs { get; set; }

    public int? Recyc { get; set; }

    public string? RecycN { get; set; }

    public string? NData { get; set; }

    public byte? ShrtCs { get; set; }

    public virtual ICollection<Book> Books { get; set; } = new List<Book>();

    public virtual _0cat? CatNavigation { get; set; }

    public virtual ICollection<Title> Titles { get; set; } = new List<Title>();
}
