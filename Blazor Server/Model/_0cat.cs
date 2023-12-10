using System;
using System.Collections.Generic;

namespace LibraryApp.Model;

public partial class _0cat
{
    public int Id { get; set; }

    public string? Name { get; set; }

    public int? Catord { get; set; }

    public byte? Lvl { get; set; }

    public virtual ICollection<_0bok> _0boks { get; set; } = new List<_0bok>();
}
